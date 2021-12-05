package com.example.redistest;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 发送订单创建消息测试
 * @author junyangwei
 * @date 2021-12-05
 */
@RestController
public class OrderPublish {
    @Resource
    RedissonClient redissonClient;

    @GetMapping("/order_publish")
    public void orderPublishTest() {
        // 初始化一个订单
        Order order = new Order();
        order.id = 1;
        order.orderId = 1122334455L;
        order.userId = 123;

        // 做一些订单业务相关业务...

        RTopic topic = redissonClient.getTopic("ORDER_CREATE");
        topic.publish(order);
    }
}
