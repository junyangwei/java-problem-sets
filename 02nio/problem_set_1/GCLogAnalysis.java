import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * GC 日志打印主类
 *  - 主要分为两个模块：
 *      1. 保证其他参数相同情况下，改变 GC 算法：串行/并行/CMC/G1，打印GC日志分析
 *          a. 使用串行GC "-XX:+UseSerialGC"
 *          b. 使用并行GC "-XX:+UseParallelGC"
 *          c. 使用CMC GC "-XX:+UseConcMarkSweepGC"
 *          4. 使用G1 GC "-XX:+UseG1GC"
 *      2. 保证使用 java8 默认GC算法(并行)的情况下，改变其他参数查看日志变化
 *          a. 修改设置的堆内存大小 -Xmx1g -Xms1g 与 -Xmx512m -Xms512m
 *          b. 修改打印日志详情 -XX:+PrintGCDetails 与 -XX:+PrintGC
 *          c. 关闭日志日期标记 -XX:+PrintGCDateStamps 使用及不使用情况
 *  - 使用的命令：
 *      1. 改变 GC 算法：
 *          a. java -Xmx1g -Xms1g -XX:+UseSerialGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:serial_gc_log GCLogAnalysis
 *          b. java -Xmx1g -Xms1g -XX:+UseParallelGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:parallel_gc_log GCLogAnalysis
 *          c. java -Xmx1g -Xms1g -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:cmc_gc_log GCLogAnalysis
 *          d. java -Xmx1g -Xms1g -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:g1_gc_log GCLogAnalysis
 *      2. 改变其他参数：
 *          a. java -Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:heap1g_default_gc_log GCLogAnalysis
 *             java -Xmx512m -Xms512m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:heap512m_default_gc_log GCLogAnalysis
 *             java -Xmx256m -Xms256m -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseSerialGC -Xloggc:heap256m_serial_gc_log GCLogAnalysis
 *          b. java -Xmx1g -Xms1g -XX:+PrintGC -XX:+PrintGCDateStamps -Xloggc:heap1g_default_gc_simple_log GCLogAnalysis
 *          c. java -Xmx1g -Xms1g -XX:+PrintGCDetails -Xloggc:heap1g_default_gc_nodate_log GCLogAnalysis
 *          d. java -Xmx1g -Xms1g -XX:+PrintGC -Xloggc:heap1g_default_gc_simple_nodate_log GCLogAnalysis
 * @author junyangwei
 * @date 2021-09-24
 */
public class GCLogAnalysis {
    /**
     * 定义一个随机数对象
     */
    private static Random random = new Random();

    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMills = System.currentTimeMillis();
        // 持续运行毫秒数；可根据需要进行修改
        long timeoutMills = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMills = startMills + timeoutMills;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分对象；进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        // 在此时间范围内，持续循环
        while (System.currentTimeMillis() < endMills) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100 * 1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束！共生成对象次数：" + counter.longValue());
    }

    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
