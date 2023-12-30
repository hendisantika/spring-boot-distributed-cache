package com.hendisantika.distributedcache.compression;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-cache
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 12/31/23
 * Time: 06:49
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@RequiredArgsConstructor
public class CompressedRedisSerializer implements RedisSerializer<byte[]> {
    private final CompressionAlgorithm compressionAlgorithm;
}
