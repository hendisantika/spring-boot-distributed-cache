package com.hendisantika.distributedcache.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.support.collections.RedisProperties;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-cache
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 12/31/23
 * Time: 06:41
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class RedisClientConfig {
    @Profile("!local")
    @Bean(destroyMethod = "shutdown")
    public ClientResources redisClientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public ClientOptions redisClientOptions() {
        return ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true).build();
    }

    @Bean
    public LettucePoolingClientConfiguration lettucePoolingClientConfiguration(
            ClientOptions redisClientOptions, ClientResources redisClientResources,
            RedisProperties redisProperties) {
        return LettucePoolingClientConfiguration.builder()
                .commandTimeout(redisProperties.getConfig().getReadTimeout())
                .poolConfig(redisProperties.getConfig().getPoolConfig())
                .clientOptions(redisClientOptions).clientResources(redisClientResources).build();
    }

    @Bean
    public RedisClusterConfiguration customRedisCluster(RedisProperties redisProperties) {
        RedisConfig newConfig = redisProperties.getConfig();
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(
                newConfig.getNodes());
        // Set up password here if necessary
        // clusterConfiguration.setPassword(...);
        return clusterConfiguration;
    }

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory(
            RedisClusterConfiguration customRedisCluster,
            LettucePoolingClientConfiguration lettucePoolingClientConfiguration) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(customRedisCluster,
                lettucePoolingClientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }
}
