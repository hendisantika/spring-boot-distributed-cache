package com.hendisantika.distributedcache.config;

import com.hendisantika.distributedcache.compression.CompressionAlgorithm;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

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
@SuppressWarnings("NullAway.Init")
@ConfigurationProperties(prefix = "cache.redis")
public class RedisProperties {
    @NotNull
    private Duration ttl;

    @NotNull
    @NestedConfigurationProperty
    private RedisConfig config;

    @NotNull
    private CompressionAlgorithm compressionAlgorithm;
}
