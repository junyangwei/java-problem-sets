package com.example.redistest;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
public class RedistestApplication {

	@Resource
	RedissonClient client;

	public static void main(String[] args) {
		SpringApplication.run(RedistestApplication.class, args);
	}

	/**
	 * 使用 Redis 写一个简单的分布式锁
	 */
	@GetMapping("/test_lock")
	public String testLock() throws InterruptedException {
		// 测试使用 redisson 封装的 tryLock 方法使用分布式锁
		for (int i = 0; i < 3; i++) {
			new Thread(() -> lockTest()).start();
		}

		// 主线程休眠1秒，分隔开两种方法打印结果
		Thread.sleep(3000);
		System.out.println();

		// 测试使用 redisson 封装的 trySet 方法使用分布式锁(setnx)
		new Thread(() -> lockTest2()).start();
		new Thread(() -> lockTest2()).start();
		new Thread(() -> lockTest2()).start();
		new Thread(() -> {
		    // 休眠1秒再调用方法，查看释放锁后是否可以再加锁
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lockTest2();
		}).start();

		return "test";
	}

	/**
	 * 使用 redisson 封装的 tryLock 方法添加分布式锁
	 */
	public void lockTest() {
		RLock lock = client.getLock("lock-test");
		try {
			boolean lockRes = lock.tryLock(10, TimeUnit.SECONDS);
			if (lockRes) {
			    // 加锁成功，进行业务逻辑处理...
				System.out.println(Thread.currentThread().getName() + "锁定");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 解锁
		if (lock.isHeldByCurrentThread()) {
			lock.unlock();
			System.out.println(Thread.currentThread().getName() + "解锁...");
		}
	}

	/**
	 * 使用 Redis setnx 命令添加分布式锁
	 * 	- setnx: 没有值则写入成功，有值则不写入
	 */
	public void lockTest2() {
	    // 加锁
		RBucket<String> bucket = client.getBucket("lock-test-2");
		boolean lockRes = bucket.trySet("1", 10, TimeUnit.SECONDS);

		// 校验是否加锁成功
		if (lockRes) {
			System.out.println(Thread.currentThread().getName() + "加锁成功...");
		} else {
		    // 加锁失败，触发分布式锁，打印信息
			System.out.println(Thread.currentThread().getName() + "使用 redis setnx 的方式触发并发锁...");
			return;
		}

		// 解锁
		bucket.delete();
		System.out.println(Thread.currentThread().getName() + "解锁成功...");
	}
}
