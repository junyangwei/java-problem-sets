package concurrency;

/**
 * synchronized 练习 —— 修饰代码块
 * @author junyangwei
 * @date 2021-10-20
 */
public class SynchronizedTest02 implements Runnable {
    int num = 0;
    Object lockObj;

    SynchronizedTest02(Object lockObj) {
        this.lockObj = lockObj;
    }

    public void incr() {
        num++;
    }

    /**
     * 使用 synchronized 对 lockObj 对象加锁
     *  - 若其它线程同一时间，也使用 synchronized 对 lockObj 对象加锁
     *  - 那么这两个线程同一时间，只有抢占到 lockObj 对象锁的线程才可以正常执行 synchronized 的代码块
     *
     * 若使用 synchronized 对 this 加锁（表示对SynchronizedTest02类对象加锁）
     *  - 那么下面这种情况，就会触发对当前类对象锁的争抢
     *  - SynchronizedTest02 test = new SynchronizedTest02(lockObj);
     *  - new Thread(test).start();
     *  - new Thread(test).start();
     */
    @Override
    public void run() {
        // 可将 lockObj 替换成 this 试试
        synchronized (lockObj) {
            System.out.println(Thread.currentThread().getName() + "争抢到了 lockObj 对象锁");
            int loop = 100_0000;
            for (int i = 0; i < loop; i++) {
                this.incr();
            }
        }
    }

    public static void main(String[] args) {
        // 定义一个Object对象，用来加锁
        Object lockObj = new Object();

        SynchronizedTest02 test = new SynchronizedTest02(lockObj);
        Thread thread = new Thread(test);
        thread.start();

        // 设置主线程与 thread 线程的 run 方法争抢同一个对象锁 lockObj
        synchronized (lockObj) {
            System.out.println(Thread.currentThread().getName() + "争抢到了 lockObj 对象锁");
            int loop = 100_0000;
            for (int i = 0; i < loop; i++) {
                test.incr();
            }
        }

        try {
            // 主线程等待 thread 线程执行完毕
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("num结果值：" + test.num);

        System.out.println("主线程退出...");
    }
}
