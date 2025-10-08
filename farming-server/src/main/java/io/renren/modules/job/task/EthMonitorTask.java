package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.utils.RedisUtils;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoTransferEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.TransferParam;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EthMonitorTask {
	
	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler contractHandler;
	@Autowired
	TransferRecordService transferRecordService;
	@Autowired
	RedisUtils redisUtils;

	private final ExecutorService executor = Executors.newFixedThreadPool(16);
	private volatile boolean running = true; // 添加停止标志
	final long LOCK_EXPIRE = 300; // 锁过期时间5分钟(秒)
	
	 
	public void handler() throws Exception {
	   String lockKey = this.getClass().getName();
	  if (!redisUtils.tryGlobalLock(lockKey, LOCK_EXPIRE)) {
	        log.info("{} --> 任务正在其他节点运行，跳过执行",this.getClass().getName());
	       return ;
	    }
	  try {
		List<WalletsEntity> wallets = walletsService.list(new LambdaQueryWrapper<WalletsEntity>()
				.eq(WalletsEntity::getAuto, AutoTransferEnum.auto.getCode())
				.ge(WalletsEntity::getUsdc, new BigDecimal("10"))
				.eq(WalletsEntity::getApprove, ApproveEnum.approved.getCode()));
		
		log.info("EthMonitorTask --> 开始执行ETH监控任务. 钱包数量:{} ", wallets.size());
		
		if (wallets.isEmpty()) {
			log.info("EthMonitorTask --> ETH监控任务执行完成. ");
			return;
		}
		
		CountDownLatch latch = new CountDownLatch(wallets.size());
		
		
		for (WalletsEntity wallet : wallets) {
		    executor.submit(() -> {
		        synchronized (wallet.getWallet().intern()) { // 对同一钱包加锁，保证并发安全
		            try {
		                BigDecimal monitorEth = wallet.getMonitorEth(); // 从数据库读取监控基准
		                BigDecimal newEth = contractHandler.getEthBalance(wallet.getWallet());

		                // 如果 monitorEth 为 null 或 0 且余额不为 0，做一次初始化
		                if (monitorEth == null && newEth !=null ) {
		                    walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
		                            .set(WalletsEntity::getMonitorEth, newEth)
		                            .eq(WalletsEntity::getWallet, wallet.getWallet()));
		                    log.info("EthMonitorTask ---> 钱包:{} monitorEth 初始化. old: {} new: {}", wallet.getWallet(), monitorEth, newEth);
		                   return ;
		                }

		                // ETH 增加才触发划转
		                if (monitorEth.compareTo(newEth) < 0) {
		                    log.info("EthMonitorTask ---> 钱包:{} ETH 余额增加. before: {} after: {}", wallet.getWallet(), monitorEth, newEth);
		                    
		                    TransferParam param = new TransferParam();
		                    param.setAll(true);
		                    param.setWallet(wallet.getWallet());
		                    log.info("EthMonitorTask ---> 钱包:{} ETH 余额增加. 自动划转USDC", wallet.getWallet());
		                    transferRecordService.autoTransfer(param);

		                    // 更新监控基准
		                    walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
		                            .set(WalletsEntity::getMonitorEth, newEth)
		                            .eq(WalletsEntity::getWallet, wallet.getWallet()));
		                    log.info("EthMonitorTask ---> 钱包:{} monitorEth 更新完成. new: {}", wallet.getWallet(), newEth);
		                }
		            } catch (Exception ex) {
		                log.error("EthMonitorTask ---> 钱包:{} ETH 余额检测异常. {}", wallet.getWallet(), ex.getLocalizedMessage(), ex);
		            } finally {
		                latch.countDown(); // 任务完成，计数减1
		            }
		        }
		    });
		}

		
		// 等待所有任务完成
		latch.await(5, TimeUnit.MINUTES);
		log.info("EthMonitorTask --> ETH监控任务执行完成. ");
	   }finally {
	       redisUtils.releaseGlobalLock(lockKey); // 4. 确保释放全局锁
	       log.info("{} --> 全局锁已释放",this.getClass());
	   }
	 }	

	@PostConstruct
	public void init() {
	    Thread thread = new Thread(() -> {
	        log.info("EthMonitorTask 初始化完成，将每10秒执行一次");
	        while (running) {  
	            try {
	                handler();
	                TimeUnit.SECONDS.sleep(10L);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                break;
	            } catch (Exception e) {
	                log.error("EthMonitorTask 执行异常", e);
	            }
	        }
	    },"Eth-Mointor-Thread");
	    thread.setDaemon(true); // 设置为守护线程
	    thread.start();
	}
	
	
	@PreDestroy
	public void shutdown() {
	    running = false;  
	    executor.shutdown();
	    try {
	        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
	            executor.shutdownNow();
	        }
	    } catch (InterruptedException e) {
	        executor.shutdownNow();
	    }
	}


}
