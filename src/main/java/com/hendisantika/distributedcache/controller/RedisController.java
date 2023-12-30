package com.hendisantika.distributedcache.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hendisantika.distributedcache.config.RedisProperties;
import com.hendisantika.distributedcache.model.FallbackDTO;
import com.hendisantika.distributedcache.model.ItemDTO;
import com.hendisantika.distributedcache.model.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping(path = "/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemType getItem(@PathVariable("itemId") String itemId) {
        try {
            byte[] cachedContent = redisTemplate.opsForValue().get(itemId);
            if (cachedContent != null) {
                return objectMapper.readValue(cachedContent, ItemDTO.class);
            }
        } catch (Exception e) {
            log.error("Error while getting item from cache", e);
        }
        // Fetch Data from the Database
        // Write Data into the Cache
        return new FallbackDTO("Cache miss;");
    }
}
