package io.renren.modules.core.controller.admin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Convert;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.common.utils.R;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoTransferEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.TransferRecordEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.param.TransferParam;
import io.renren.modules.core.param.TransferRecordParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.TransferRecordVO;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.utils.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/admin/transfer")
public class TransferRecordController extends AbstractController{
	@Autowired
	TransferRecordService transferRecordService; 
	@Autowired
	ContractHandler handler;
	@Autowired
	WalletsService walletsService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	WithDrawService withDrawService;
	@Autowired
	SysUserService sysUserService;
	
	
	@PostMapping("list")
	public R list(@RequestBody TransferRecordParam param) throws Exception {
         Page<TransferRecordVO> page = transferRecordService.listPage(param);
         BigDecimal totalUsdc = transferRecordService.sumRecord(param);
         return R.ok(page).put("totalUsdc", totalUsdc);
	}
	
	@PostMapping("usdc")
	@RequiresPermissions("admin:transfer:usdc")
	public R transfer(@RequestBody TransferParam param) throws Exception {
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		if(walletEntity.getApprove() == ApproveEnum.unApprove.getCode()) throw new RRException("wallet not approve");
		PoolsEntity walletPools = poolsService.getById(walletEntity.getPoolsId());
		if(walletPools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(walletPools.getCreateUserId() != getUser().getUserId() && walletPools.getOwnerId()!= getUser().getUserId() ) {
			throw new RRException("You do not have permission to operate this data");
		}
		SysUserEntity user = sysUserService.getById(getUser().getUserId());
		if(StringUtils.isBlank(user.getGoogleAuth())){
			throw new RRException("Google Auth Code not build.");
		}
		// 6. 验证Google验证码（如果启用）
        if (!new GoogleAuthenticator().check_code(AESUtils.decrypt(user.getGoogleAuth()), 
            Long.parseLong(param.getGoogleAuthCode()),
            System.currentTimeMillis())) {
            return R.error("Google验证码不正确");
        }
        
		BigDecimal balanceOf = handler.getUsdcBalance(walletEntity.getWallet());
		log.info("transfer wallet:{} balanceOf:{}",walletEntity.getWallet(),balanceOf);
		if(balanceOf.compareTo(new BigDecimal("10")) < 0) throw new RRException("Wallet balance is less than 10U");
		if(!param.getAll() && (param.getAmount() == null || param.getAmount().compareTo(new BigDecimal("10")) <=0)) throw new RRException("input transfer amount is 10U");
		BigDecimal allowance = handler.allowance(walletEntity.getWallet(), walletEntity.getApproveWallet());
		if(param.getAll() ? allowance.compareTo(balanceOf) < 0 : allowance.compareTo(param.getAmount()) < 0) throw new RRException("The wallet has not yet executed the permit method. ");
		
		TransferRecordEntity recordEntity = new TransferRecordEntity();
		recordEntity.setWallet(walletEntity.getWallet());
		recordEntity.setReciverWallet(walletEntity.getReciverWallet());
		recordEntity.setPoolsId(walletEntity.getPoolsId());
		recordEntity.setStatus(TransferEnum.no.getCode());
		recordEntity.setAuto(AutoTransferEnum.manual.getCode());
		recordEntity.setInviteId(walletEntity.getInviteId());
		recordEntity.setUsdc(param.getAll()?balanceOf : param.getAmount());
		transferRecordService.save(recordEntity);
//		if(walletEntity.getReals() == RealEnum.virtual.getCode()) {
//			BigDecimal newBalanceOf = walletEntity.getVirtualUsdc().subtract(param.getAll()?balanceOf : param.getAmount());
//			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getVirtualUsdc, newBalanceOf).eq(WalletsEntity::getWallet, walletEntity.getWallet()));
//			log.info("虚拟钱包:{} 划转USDT:{} 成功. ",walletEntity.getWallet(),param.getAll()?balanceOf : param.getAmount());
//			return R.ok();
//		}
		String spenderAddress = walletEntity.getApproveWallet();
		PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getApproveWallet, spenderAddress)) ;
		String spenderKey = pools.getApproveKey();
		
		String hash = null ;
		try {
			BigDecimal eth = handler.getEthBalance(spenderAddress);//检查余额
			BigInteger maxFeePerGas =  handler.getMaxFeePerGas();
			BigInteger ethgas =  maxFeePerGas.multiply(Constants.gasLimit);
			BigDecimal _balance =  Convert.fromWei(new BigDecimal(ethgas), Convert.Unit.ETHER);
			if(eth.compareTo(_balance) <=0) {
				throw new RRException("授权钱包ETH不足,当前余额:" +eth +",实际需要大于:" +_balance  );
			}
			hash = param.getAll() ? 
					handler.transferPermitAllUsdc(walletEntity.getWallet(),walletEntity.getReciverWallet(),spenderAddress,spenderKey ) : 
					handler.transferPermitUsdc(walletEntity.getWallet(),walletEntity.getReciverWallet(),spenderAddress,spenderKey, param.getAmount());
		if(hash == null)  throw new RRException("Transfer unknow exception.");
		}catch(Exception ex) {
			transferRecordService.update(new LambdaUpdateWrapper<TransferRecordEntity>()
					.set(TransferRecordEntity::getStatus, TransferEnum.error.getCode())
					.set(TransferRecordEntity::getRemark, ex.getMessage())
					.eq(TransferRecordEntity::getId, recordEntity.getId()));
			log.info("钱包:{} 划转USDC:{} 失败  ",walletEntity.getWallet(),param.getAll()?balanceOf : param.getAmount());
			 throw new RRException(ex.getMessage());
		}
		BigDecimal transferUsdc = param.getAll()?balanceOf : param.getAmount();
		transferRecordService.update(new LambdaUpdateWrapper<TransferRecordEntity>()
				.set(TransferRecordEntity::getHash, hash)
				.set(TransferRecordEntity::getStatus, TransferEnum.yes.getCode())
				.set(TransferRecordEntity::getRemark,"SUCCESS")
				.eq(TransferRecordEntity::getId, recordEntity.getId()));
		log.info("钱包:{} 划转USDC:{} 成功. hash:{} - 操作用户:{} ",walletEntity.getWallet(),transferUsdc,hash,getUser().getUsername());
		//自动设置虚拟USDC 金额
		BigDecimal newVirtualUsdc = walletEntity.getVirtualUsdc().add(transferUsdc);
		walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getVirtualUsdc,newVirtualUsdc ).eq(WalletsEntity::getWallet, walletEntity.getWallet()));
		log.info("钱包:{} 划转USDC:{} 成功. 完成自动设置虚拟USDC ",walletEntity.getWallet(),transferUsdc);
		
		//滞留的提现  - 自动设置为体现成功
		List<WithDrawEntity> approveingWithdraw = withDrawService.list(new LambdaQueryWrapper<WithDrawEntity>()
				.eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaling.getCode())
				.eq(WithDrawEntity::getWallet, walletEntity.getWallet()));
		if(CollectionUtils.isNotEmpty(approveingWithdraw)) {
			List<Long> ids = approveingWithdraw.stream().map(WithDrawEntity::getId).collect(Collectors.toList());
			withDrawService.update(new LambdaUpdateWrapper<WithDrawEntity>()
					.set(WithDrawEntity::getStatus, WithdrawStatusEnum.Success.getCode())
					.eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaling.getCode())
					.in(WithDrawEntity::getId, ids));
			BigDecimal vusdc = approveingWithdraw.stream().map(WithDrawEntity::getUsdc).reduce(BigDecimal.ZERO,BigDecimal::add);
			newVirtualUsdc = newVirtualUsdc.add(vusdc);
			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getVirtualUsdc,newVirtualUsdc ).eq(WalletsEntity::getWallet, walletEntity.getWallet()));
			log.info("钱包:{} 划转USDC:{} 成功. 存在滞留未审批的提现订单ID:{} ，设置为提现成功. 最终虚拟USDC:{}  ",walletEntity.getWallet(),transferUsdc,JSON.toJSONString(ids),newVirtualUsdc);
		}
		walletsService.refresh(walletEntity);//刷新钱包
		return R.ok();
		
	}
	
}
