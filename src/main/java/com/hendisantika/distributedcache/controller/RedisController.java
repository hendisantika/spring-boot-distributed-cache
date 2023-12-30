package com.hendisantika.distributedcache.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hendisantika.distributedcache.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-distributed-cache
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 12/31/23
 * Time: 06:54
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class RedisController {
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisProperties redisProperties;

}
