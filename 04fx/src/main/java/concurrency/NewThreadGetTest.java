package concurrency;

import java.util.concurrent.*;

/**
 * 开启一个新的线程，调用一个方法，在主线程获取到新线程的返回值时再退出
 * @author junyangwei
 * @date 2021-10-18
 */
public class NewThreadGetTest {
    /**
     * 开始时间
     */
    long start = 0;
    /**
     * 待计算值
     */
    int calValue = 3;
    /**
     * 新线程计算结果值（斐波那契数列值）
     */
    int resultNum = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NewThreadGetTest test = new NewThreadGetTest();
        System.out.println("主线程名称：" + Thread.currentThread().getName());

        test.start = System.currentTimeMillis();

        /*
        方法一：使用 ExecutorService 的 submit(Callable<T> task) 方法
            - 构造线程池
            - 定义执行方法
            - 等待异步执行结果，关闭线程池，并打印
         */
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        Future<Integer> result = executorService1.submit(() -> fibo(test.calValue));
        test.resultNum = result.get();
        executorService1.shutdown();
        test.printResult("ExecutorService对象方法submit(Callable<T> task)");

        test.start = System.currentTimeMillis();
        test.calValue++;

        /*
        方法二：使用 ExecutorService 的 submit(Runnable task, T result) 方法
            - 构造线程池
            - 构建实现了 Runnable 的类对象 task （类属性定义了存储结果的变量resultNum）
            - 定义执行对象以及结果类型
            - 等待异步执行结果，关闭线程池，并打印
         */
        ExecutorService executorService2 = Executors.newFixedThreadPool(1);
        Task task = new Task(test.calValue);
        Future<?> result2 = executorService2.submit(task, task.resultNum);
        result2.get();  // 调用 get 方法，让主线程等待线程执行完毕，确保获取到结果值
        test.resultNum = task.resultNum;
        executorService2.shutdown();
        test.printResult("ExecutorService对象方法submit(Runnable task, Integer result)");

        test.start = System.currentTimeMillis();
        test.calValue++;

        /*
        方法三：使用 ExecutorService 的 submit(Runnable task) 方法
            - 构造线程池
            - 构建实现了 Runnable 的类对象 task （类属性定义了存储结果的变量resultNum）
            - 定义执行对象以及结果类型
            - 等待异步执行结果，关闭线程池，并打印
         */
        ExecutorService executorService3 = Executors.newCachedThreadPool();
        Task task3 = new Task(test.calValue);
        Future<?> result3 = executorService3.submit(task3);
        result3.get();  // 调用 get 方法，让主线程等待线程执行完毕，确保获取到结果值
        test.resultNum = task3.resultNum;
        executorService3.shutdown();
        test.printResult("ExecutorServer对象方法submit(Runnable task)");

        test.start = System.currentTimeMillis();
        test.calValue++;

        /*
        方法四：使用CountdownLatch，主线程等待所有子线程执行完毕
         */
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatchTask task4 = new CountDownLatchTask(latch, test.calValue);
        Thread thread1 = new Thread(task4);
        thread1.start();
        latch.await();  // 等待子线程执行完毕
        test.resultNum = task4.resultNum;
        test.printResult("CountDownLatch");

        test.start = System.currentTimeMillis();
        test.calValue++;

        /*
        方法五：使用CompletableFuture
            - CompletableFuture 还可以 CountdownLatch 结合使用
            - CompletableFuture 的 runAsync 方法还可以自定义执行的线程池
         */
        Task task5 = new Task(test.calValue);
        CompletableFuture<Void> future = CompletableFuture.runAsync(task5);
        future.get(); // 等待新线程执行结果
        test.resultNum = task5.resultNum;
        test.printResult("CompletableFuture");

        test.start = System.currentTimeMillis();
        test.calValue++;

        System.out.println("主线程最终退出...");
        return;
    }

    private void printResult(String methodName) {
        System.out.println("[" + methodName + "] 计算值为: " + this.calValue + ", 异步计算结果为: " + this.resultNum);
        System.out.println("[" + methodName + "] 使用时间: " + (System.currentTimeMillis() - this.start) + "ms");
        System.out.println("主线程退出[" + methodName + "]方法结果打印...");
        System.out.println();
    }

    public static int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }
}

class Task implements Runnable {
    int resultNum = 0;
    private int calValue;

    Task(int calValue) {
        this.calValue = calValue;
    }

    @Override
    public void run() {
        resultNum = NewThreadGetTest.fibo(this.calValue);
    }
}

class CountDownLatchTask implements Runnable {
    int resultNum = 0;
    private int calValue;
    private CountDownLatch latch;

    CountDownLatchTask(CountDownLatch latch, int calValue) {
        this.latch = latch;
        this.calValue = calValue;
    }

    @Override
    public void run() {
        resultNum = NewThreadGetTest.fibo(this.calValue);
        this.latch.countDown();
    }
}
