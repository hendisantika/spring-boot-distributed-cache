package com.hendisantika.distributedcache.controller;

import com.hendisantika.distributedcache.config.RedisProperties;
import com.hendisantika.distributedcache.support.RedisClusterIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RedisControllerIT extends RedisClusterIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RedisTemplate<String, byte[]> redisTemplate;

    @Autowired
    private RedisProperties redisProperties;

    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void storesAnItemAndReadsItBackFromTheCluster() throws Exception {
        MockMvc mockMvc = mockMvc();

        mockMvc.perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":42,"name":"Kopi Gayo","price":19.99}"""))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/items/{itemId}", "42"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Kopi Gayo"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    void appliesTheConfiguredTimeToLive() throws Exception {
        mockMvc().perform(post("/api/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":77,"name":"Teh Tarik","price":5.50}"""))
                .andExpect(status().isOk());

        Long ttl = redisTemplate.getExpire("77", TimeUnit.SECONDS);

        assertThat(ttl).isNotNull()
                .isPositive()
                .isLessThanOrEqualTo(redisProperties.getTtl().toSeconds());
    }

    @Test
    void compressesTheStoredValueRatherThanStoringRawJson() throws Exception {
        String json = """
                {"id":88,"name":"Nasi Goreng Kampung Spesial","price":42.00}""";

        mockMvc().perform(post("/api/v1/items").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        byte[] stored;
        try (RedisClusterConnection connection =
                     redisTemplate.getRequiredConnectionFactory().getClusterConnection()) {
            stored = connection.stringCommands().get("88".getBytes(StandardCharsets.UTF_8));
        }

        assertThat(stored).isNotNull();
        assertThat(new String(stored, StandardCharsets.UTF_8)).isNotEqualTo(json);
    }

    @Test
    void fallsBackWhenTheItemIsNotCached() throws Exception {
        mockMvc().perform(get("/api/v1/items/{itemId}", "does-not-exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Cache miss;"));
    }
}
