package io.renren;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RateLimiterTest {
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(10, 1, TimeUnit.SECONDS);
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 200; i++) {
            executorService.submit(() -> {
                rateLimiter.acquire();
                System.out.println("任务开始");
                System.out.println("任务结束");
            });
        }

    }
}
