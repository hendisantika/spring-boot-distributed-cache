package com.hendisantika.distributedcache.config;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
}
