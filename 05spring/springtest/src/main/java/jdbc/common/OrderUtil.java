package jdbc.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author junyangwei
 * @date 2021-11-13
 */
public class OrderUtil {
    static final int MAX_INCR_NUM = 9999;

    static final AtomicInteger incrAtomic = new AtomicInteger();

    /**
     * 生成订单ID
     * 算法：时间戳 + 线程ID与100的余数 + 自增ID 的形式
     */
    public static long orderIdGenerator() {
        // 获取系统时间戳（毫秒）
        Long time = System.currentTimeMillis();

        // 去除前两位经常不变的
        String ts = time.toString().substring(2);

        // 获取线程ID值，计算方式为：线程ID与100的余数
        long threadId = Thread.currentThread().getId() % 100;

        return Long.valueOf(ts) * 1000000 + threadId * 10000 + getIncrNum();
    }

    /**
     * 自增值的取值范围为[0, 9999]
     * 这个取值范围的好处是可以直接通过
     * @return
     */
    public static long getIncrNum() {
        int incrNum = OrderUtil.incrAtomic.incrementAndGet();

        if (incrNum > MAX_INCR_NUM) {
            return incrNum % MAX_INCR_NUM;
        }

        return incrNum;
    }

    /**
     * 单元测试方法
     */
    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                System.out.println(Thread.currentThread().getId() + " " + OrderUtil.orderIdGenerator());
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                System.out.println(Thread.currentThread().getId() + " " + OrderUtil.orderIdGenerator());
            }
        });
        thread1.start();
        thread2.start();

        for (int i = 0; i < 10000; i++) {
            System.out.println(OrderUtil.orderIdGenerator());
        }
    }
}
