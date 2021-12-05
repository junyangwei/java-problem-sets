package com.example.redistest;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单实体类
 * @author junyangwei
 * @date 2021-12-05
 */
@Data
public class Order implements Serializable {
    public Integer id;
    public Long orderId;
    public Integer userId;
}
