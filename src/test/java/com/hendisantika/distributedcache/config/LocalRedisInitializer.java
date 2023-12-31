package com.hendisantika.distributedcache.config;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.SocketAddressResolver;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-cache
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 12/31/23
 * Time: 07:10
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class LocalRedisInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Set<Integer> redisClusterPorts = Set.of(7000, 7001, 7002, 7003, 7004,
            7005);
    private static final List<String> nodes = new ArrayList<>();
    private static final ConcurrentMap<Integer, Integer> redisClusterNotPortMapping = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, SocketAddress> redisClusterSocketAddresses = new ConcurrentHashMap<>();

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        redisLocalSetup(applicationContext);
    }

    private void redisLocalSetup(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        GenericContainer<?> redis = new GenericContainer<>(
                DockerImageName.parse("grokzen/redis-cluster:6.0.7"))
                .withExposedPorts(redisClusterPorts.toArray(new Integer[0]));
        redis.start();
        String hostAddress = redis.getHost();
        redisClusterPorts.forEach(port -> {
            Integer mappedPort = redis.getMappedPort(port);
            redisClusterNotPortMapping.put(port, mappedPort);
            nodes.add(hostAddress + ":" + mappedPort);
        });
        setProperties(environment, "cache.redis.config.nodes", nodes);
    }

    @Bean(destroyMethod = "shutdown")
    public ClientResources redisClientResources() {
        final SocketAddressResolver socketAddressResolver = new SocketAddressResolver() {
            @Override
            public SocketAddress resolve(RedisURI redisURI) {
                Integer mappedPort = redisClusterNotPortMapping.get(redisURI.getPort());
                if (mappedPort != null) {
                    SocketAddress socketAddress = redisClusterSocketAddresses.get(mappedPort);
                    if (socketAddress != null) {
                        return socketAddress;
                    }
                    redisURI.setPort(mappedPort);
                }
                redisURI.setHost(DockerClientFactory.instance().dockerHostIpAddress());
                SocketAddress socketAddress = super.resolve(redisURI);
                redisClusterSocketAddresses.putIfAbsent(redisURI.getPort(), socketAddress);
                return socketAddress;
            }
        };
        return ClientResources.builder().socketAddressResolver(socketAddressResolver).build();
    }

    private void setProperties(ConfigurableEnvironment environment, String name, Object value) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> source = sources.get(name);
        if (source == null) {
            source = new MapPropertySource(name, new HashMap<>());
            sources.addFirst(source);
        }
        ((Map<String, Object>) source.getSource()).put(name, value);
    }
}
