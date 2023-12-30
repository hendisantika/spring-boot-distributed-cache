package com.hendisantika.distributedcache.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-cache
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 12/31/23
 * Time: 06:44
 * To change this template use File | Settings | File Templates.
 */
@Data
@Validated
@RequiredArgsConstructor(onConstructor_ = {@ConstructorBinding})
public class RedisConfig {
    private final Duration readTimeout;
    private final Duration connectionTimeout;
    private final int retry;

    @NotEmpty
    private final List<String> nodes;
    private final int maxTotalPool;
    private final int maxIdlePool;
    private final int minIdlePool;
    private final Duration maxWait;

    public <T> GenericObjectPoolConfig<T> getPoolConfig() {
        GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(maxTotalPool);
        config.setMaxIdle(maxIdlePool);
        config.setMinIdle(minIdlePool);
        config.setMaxWait(maxWait);
        return config;
    }
}
