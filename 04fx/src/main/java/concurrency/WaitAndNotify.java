package concurrency;

/**
 * wait 和 notify 方法调用练习
 * @author junyangwei
 * @date 2021-10-21
 */
public class WaitAndNotify {

    public synchronized void waitTest() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + "执行 wait 方法.");
        wait();
        System.out.println(Thread.currentThread().getName() + "结束了wait.");
    }

    public synchronized void notifyTest() {
        notify();
        System.out.println(Thread.currentThread().getName() + "调用了notify.");
    }


    public static void main(String[] args) {
        WaitAndNotify test = new WaitAndNotify();
        new Thread(() -> {
            try {
                test.waitTest();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            test.notifyTest();
        }).start();

        System.out.println("主线程退出...");
    }
}
