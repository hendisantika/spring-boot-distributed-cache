package com.hendisantika.distributedcache.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base class for tests that need the application wired against a real Redis Cluster.
 */
@SpringBootTest
public abstract class RedisClusterIntegrationTest {

    @DynamicPropertySource
    static void redisClusterNodes(DynamicPropertyRegistry registry) {
        RedisClusterTestContainer.registerNodes(registry);
    }
}
