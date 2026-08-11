package com.hendisantika.distributedcache;

import com.hendisantika.distributedcache.support.RedisClusterIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class SpringBootDistributedCacheApplicationIT extends RedisClusterIntegrationTest {

    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate;

    @Test
    void contextLoadsAndTalksToTheCluster() {
        assertThat(redisTemplate.getConnectionFactory()).isNotNull();
        assertThat(redisTemplate.getConnectionFactory().getConnection().ping()).isEqualTo("PONG");
    }
}
