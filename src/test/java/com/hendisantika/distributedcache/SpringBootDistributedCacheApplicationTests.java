package com.hendisantika.distributedcache;

import com.hendisantika.distributedcache.config.LocalRedisInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = "com.hendisantika.distributedcache")
@ConfigurationPropertiesScan(basePackages = "com.hendisantika.distributedcache")
class SpringBootDistributedCacheApplicationTests {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringBootDistributedCacheApplication.class).initializers(new LocalRedisInitializer())
                .run(args);
    }

}
