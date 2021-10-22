package concurrency;

/**
 * synchronized 练习 —— 修饰实例方法、修饰静态方法
 * @author junyangwei
 * @date 2021-10-20
 */
public class SynchronizedTest01 implements Runnable {
    int num = 0;
    static int num02 = 0;

    /**
     * synchronized 修饰类的实例方法（保证多个线程对同一个对象属性修改时，是线程安全的）
     * 可以试试把 synchronized 去掉看看结果是否和预期不符
     */
    public synchronized int incrAndGet() {
        return num++;
    }

    /**
     * synchronized 修饰静态方法（保证多个线程对同一个类的静态属性修改时，是线程安全的）
     * 可以试试把 synchronized 去掉看看结果是否和预期不符
     */
    public synchronized static int incrAndGet02() {
        return ++num02;
    }

    @Override
    public void run() {
        int loop = 10_0000;
        for (int i = 0; i < loop; i++) {
            this.incrAndGet();
            SynchronizedTest01.incrAndGet02();
        }
    }

    public static void main(String[] args) {
        SynchronizedTest01 test = new SynchronizedTest01();
        Thread thread1 = new Thread(test);
        Thread thread2 = new Thread(test);
        thread1.start();
        thread2.start();

        try {
            // 主线程等待 thread1, thread2 线程执行完毕
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("num结果值：" + test.num + ", num02结果值：" + SynchronizedTest01.num02);

        System.out.println("主线程退出...");
    }
}
