package io.renren.common.utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ExecutorUtis {
	 private static ThreadPoolExecutor executor;
	 static {
	        int nThreads = 2 * Runtime.getRuntime().availableProcessors() + 1;
	        executor = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(2000), (r, executor) -> {
	            log.warn("ExecutorUtis thread pool full");
	            try {
	                Thread.sleep(5 * 1000);
	            } catch (InterruptedException e) {
	                log.error(e.getMessage(), e);
	            }
	            submit(r);
	        });
	    }
	 
	  public static <T> List<Future<T>> invokeAll(List<Callable<T>> callableList, long timeout, TimeUnit unit) throws InterruptedException {
	        return executor.invokeAll(callableList, timeout, unit);
	   }

     public static void submit(Runnable r) {
        executor.execute(r);
    }
 

}
