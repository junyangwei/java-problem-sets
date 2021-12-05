package com.example.redistest;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 订单监听器测试，HardCode 订单创建 "ORDER_CREATE"t
 * @author junyangwei
 * @date 2021-12-05
 */
@Configuration
public class OrderListener implements ApplicationRunner {
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        System.out.println("开启订单创建监听...");
        RTopic topic = redissonClient.getTopic("ORDER_CREATE");
        topic.addListener(Order.class, (channel, msg) -> {
            System.out.println("Redisson监听器收到消息:" + msg.toString());
        });
    }
}
