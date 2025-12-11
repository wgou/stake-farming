package io.renren.modules.job.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.renren.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public abstract  class BaseTask implements ITask{
    final long LOCK_EXPIRE = 300; // 锁过期时间5分钟(秒)
	@Autowired
	RedisUtils redisUtils;
	
	public void proccess(String params) throws Exception {
		String lockKey ="farming02_" + this.getClass().getName();
	  if (!redisUtils.tryGlobalLock(lockKey, LOCK_EXPIRE)) {
	        log.info("{} --> 任务正在其他节点运行，跳过执行",this.getClass().getName());
	       return ;
	    }
	    try {
	    	  log.info("{} --> 成功获取全局锁，开始执行任务",this.getClass());
		        Runtime.getRuntime().addShutdownHook(new Thread(() -> {// 2. 成功获取锁后，立即注册shutdown hook
		            if (redisUtils.isGlobalLocked(lockKey)) {
		                redisUtils.delete(lockKey);
		                log.warn("{} --> 进程关闭，强制释放Redis锁",this.getClass());
		            }
		        }));
		        handler(params);
	    }finally {
	        redisUtils.releaseGlobalLock(lockKey); // 4. 确保释放全局锁
	        log.info("{} --> 全局锁已释放",this.getClass());
	    }
	}

	abstract void handler(String params)throws Exception ;
	 


}
