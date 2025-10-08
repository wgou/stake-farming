package io.renren.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
	 @Override
    public TaskExecutor getAsyncExecutor() {
        // 创建一个线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数（池中保持的最小线程数）
        executor.setCorePoolSize(5);
        // 设置最大线程数（池中可容纳的最大线程数）
        executor.setMaxPoolSize(10);
        // 设置线程队列的容量
        executor.setQueueCapacity(25);
        // 设置线程池维护线程所允许的空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置线程名称前缀，便于调试和日志记录
        executor.setThreadNamePrefix("Async-");
        // 初始化线程池
        executor.initialize();
        return executor;
    }
}
