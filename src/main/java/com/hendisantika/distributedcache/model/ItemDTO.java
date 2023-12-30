package com.hendisantika.distributedcache.model;

import lombok.Data;

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
@Data
public class ItemDTO implements ItemType {
    private final long id;
    private final String name;
    private final double price;
}
