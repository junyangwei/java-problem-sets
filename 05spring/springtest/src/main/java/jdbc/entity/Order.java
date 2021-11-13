package jdbc.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author junyangwei
 * @date 2021-11-13
 */
@Data
public class Order {
    /**
     * 自增ID
     */
    private int id;
    /**
     * 用户ID
     */
    private int userId;
    /**
     * 订单ID
     */
    private long orderId;
    /**
     * 总原价
     */
    private double originAmount;
    /**
     * 总优惠价(支付)价
     */
    private double amount;
    /**
     * 订单支付状态 0待支付 1已支付 2完全退款
     */
    private int orderStatus;
    /**
     * 状态 0无效 1有效
     */
    private int status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
