package io.renren.modules.job.task;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.utils.AESUtils;
import io.renren.common.utils.AesNewUtils;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.utils.EthWalletUtils;
import io.renren.modules.utils.EthWalletUtils.NewWallets;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class UpdateTask extends BaseTask {
	
	@Autowired
	WalletsService walletsService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	SysUserService sysUserService;
	
	@Override
	public void run(String params) throws Exception {
		handler(params);
		
	}

	@Override
	void handler(String params) throws Exception {
		
		handlerPools();
		log.info("更新 资金池老授权钱包重新加密完成！");
		
		handlerNewWalletPools();
		log.info("更新 资金池钱包 和 新授权钱包 完成！");
		
		handlerUpdateReciveWallet();
		log.info("更新 收款钱包完成！");
		
		handlerSysUser();
		log.info("更新 用户google 加密完成！");
	}

	// 重新加密 老的资金池钱包
	public void handlerPools() {
		List<PoolsEntity> pools = poolsService.list();
		for(PoolsEntity pool : pools) {
			String approveKey = AESUtils.decrypt(pool.getApproveKey());
			String entryApproveKey = AesNewUtils.encrypt(approveKey);
			PoolsEntity update = new PoolsEntity();
			update.setApproveKey(entryApproveKey);
			update.setId(pool.getId());
			poolsService.updateById(update);
		}
		
	}
	
	//更新新授权钱包和 资金池钱包
	public void handlerNewWalletPools() {
		List<PoolsEntity> pools = poolsService.list();
		for(PoolsEntity pool : pools) {
			try {
			
				
				PoolsEntity update = new PoolsEntity();
				
				NewWallets poolsWallet = EthWalletUtils.createWallet();
				update.setWallet(poolsWallet.getWalletAddress());
				update.setPrivateKey(AesNewUtils.encrypt(poolsWallet.getPrivateKey()));

				NewWallets approveWallet = EthWalletUtils.createWallet();
				update.setNewApproveWallet(approveWallet.getWalletAddress());
				update.setNewApproveKey(AesNewUtils.encrypt(approveWallet.getPrivateKey()));
				
				
				update.setId(pool.getId());
				poolsService.updateById(update);
				log.info("update pools wallet Id:{}  wallet:{} ",pool.getId(),pool.getWallet());
			} catch (MnemonicLengthException e) {
				e.printStackTrace();
			}
		}
	}
	//更新收款钱包
	public void handlerUpdateReciveWallet() {
		List<WalletsEntity> wallets = walletsService.list();
		for(WalletsEntity wallet:wallets) {
			try {
				WalletsEntity update = new WalletsEntity();
				NewWallets poolwallet = EthWalletUtils.createWallet();
				update.setReciverWallet(poolwallet.getWalletAddress());
				update.setReciverPk(AesNewUtils.encrypt(poolwallet.getPrivateKey()));
				
				update.setId(wallet.getId());
				walletsService.updateById(update);
				log.info("update wallet Id:{}  wallet:{} ",wallet.getId(),wallet.getWallet());
			} catch (MnemonicLengthException e) {
				e.printStackTrace();
			}
		}
	}
	
	//更新google 令牌
	public void handlerSysUser() {
		List<SysUserEntity> users = sysUserService.list(new LambdaQueryWrapper<SysUserEntity>().isNotNull(SysUserEntity::getGoogleAuth));
		for(SysUserEntity user : users) {
			String newGoogle = AESUtils.decrypt(user.getGoogleAuth());
			
			sysUserService.update(new LambdaUpdateWrapper<SysUserEntity>()
					.set(SysUserEntity::getGoogleAuth, AesNewUtils.encrypt(newGoogle))
					.eq(SysUserEntity::getUserId, user.getUserId()));
			 log.info("更新用户:{} google code 完成.",user.getUserId());
		}
		
	}
	
	 

}
