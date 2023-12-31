package com.hendisantika.distributedcache.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
}
