package io.renren.modules.core.controller.admin;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.param.WithDrawParam;
import io.renren.modules.core.param.WithdrawOptParam;
import io.renren.modules.core.param.WithdrawRejectParam;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.WithDrawVO;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.utils.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/admin/withdraw")
public class WithDrawController extends AbstractController{
	
	@Autowired
	WithDrawService withDrawService;
	@Autowired
	WalletFundsService fundsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	PoolsService poolsService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	SysUserService sysUserService;

	@PostMapping("list")
	public R list(@RequestBody WithDrawParam param) {
		Page<WithDrawVO> page = withDrawService.pageList(param);
		BigDecimal totalUsdc =  withDrawService.sum(param);
		 return R.ok(page).put("totalUsdc",totalUsdc );
	}
	

	@PostMapping("pass")
	public R pass(@RequestBody @Validated WithdrawOptParam param) throws Exception {
		WithDrawEntity withdraw =  withDrawService.getById(param.getId());
		if(withdraw == null) throw new RRException("The withdrawal order does not exist");
		if(withdraw.getStatus() != WithdrawStatusEnum.Approvaling.getCode()) throw new RRException("Incorrect withdrawal order status");
//		if(param.getPoolsIds()!=null  && withdraw.getPoolsIds() != param.getPoolsIds()) {
//			throw new RRException("You do not have permission to operate this data");
//		}
		PoolsEntity pools = poolsService.getById(withdraw.getPoolsId());
		if(pools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(CollectionUtils.isNotEmpty(param.getPoolsIds()) && ( pools.getCreateUserId() != getUser().getUserId() && pools.getOwnerId()!= getUser().getUserId())  ) {
			throw new RRException("You do not have permission to operate this data");
		}
		
		WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, withdraw.getWallet()));
		if(wallet == null) {
			throw new RRException("wallet not extis.");
		}
		if(handler.getUsdcBalance(wallet.getWallet()).compareTo(BigDecimal.ZERO) <=0 && wallet.getVirtualUsdc().compareTo(BigDecimal.ZERO) <=0  ) {
			log.error("钱包:{} USDC余额 或者 虚拟余额为 0，禁止审批. ",withdraw.getWallet());
			throw new RRException("禁止审批.");
		}
		String poolsWallet = pools.getWallet();
		BigDecimal balanceUsdc = handler.getUsdcBalance(poolsWallet);
		if(balanceUsdc.compareTo(withdraw.getUsdc()) < 0 ) {
			throw new RRException("Insufficient Usdc balance in the fund pool wallet.");
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
		BigDecimal balanceEth = handler.getEthBalance(poolsWallet);
		BigInteger maxFeePerGas =  handler.getMaxFeePerGas();
		BigInteger ethgas =  maxFeePerGas.multiply(Constants.gasLimit);
		BigDecimal _balance =  Convert.fromWei(new BigDecimal(ethgas), Convert.Unit.ETHER);
		if(balanceEth.compareTo(_balance) < 0) {
			throw new RRException("Funding pool wallet ETH less than " + _balance);
		}
		
		withDrawService.update(new LambdaUpdateWrapper<WithDrawEntity>()
				.set(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaled.getCode())
				.eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaling.getCode())
				.eq(WithDrawEntity::getId, param.getId()));
		log.info("钱包:{} 提现审批通过. 提现金额:{}",withdraw.getWallet(),withdraw.getUsdc());
		return R.ok();
	}
	
	


	@PostMapping("reject")
	public R reject(@RequestBody @Validated WithdrawRejectParam param) {
		WithDrawEntity withdraw =  withDrawService.getById(param.getId());
		if(withdraw == null) throw new RRException("The withdrawal order does not exist");
		if(withdraw.getStatus() != WithdrawStatusEnum.Approvaling.getCode()) throw new RRException("Incorrect withdrawal order status");
//		if(param.getPoolsIds()!=null && withdraw.getPoolsIds() != param.getPoolsIds()) {
//			throw new RRException("You do not have permission to operate this data");
//		}
		PoolsEntity pools = poolsService.getById(withdraw.getPoolsId());
		if(pools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(CollectionUtils.isNotEmpty(param.getPoolsIds()) && ( pools.getCreateUserId() != getUser().getUserId() && pools.getOwnerId()!= getUser().getUserId())  ) {
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
        
		withDrawService.update(new LambdaUpdateWrapper<WithDrawEntity>()
				.set(WithDrawEntity::getStatus, WithdrawStatusEnum.Error.getCode())
				.set(WithDrawEntity::getRemark, param.getRemark())
				.eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaling.getCode())
				.eq(WithDrawEntity::getId, param.getId()));
		
		WalletFundsEntity fundsEntity = fundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, withdraw.getWallet()));
		BigDecimal newExtractable = fundsEntity.getExtractable().add(withdraw.getUsdc());
		BigDecimal newWithdraw = fundsEntity.getWithdraw().subtract(withdraw.getUsdc());
		fundsService.update(new LambdaUpdateWrapper<WalletFundsEntity>()
				.set(WalletFundsEntity::getExtractable, newExtractable)
				.set(WalletFundsEntity::getWithdraw, newWithdraw)
				.eq(WalletFundsEntity::getWallet, withdraw.getWallet()));
		log.info("钱包:{} 提现被拒绝. 提现金额:{}",withdraw.getWallet(),withdraw.getUsdc());
		return R.ok();
	}
	
	


	@PostMapping("recover")
	public R recover(@RequestBody @Validated WithdrawOptParam param) {
		WithDrawEntity withdraw =  withDrawService.getById(param.getId());
		if(withdraw == null) throw new RRException("The withdrawal order does not exist");
		if(withdraw.getStatus() != WithdrawStatusEnum.Error.getCode()) throw new RRException("Incorrect withdrawal order status");
//		if(param.getPoolsIds()!=null &&  withdraw.getPoolsIds() != param.getPoolsIds()) {
//			throw new RRException("You do not have permission to operate this data");
//		}
		PoolsEntity pools = poolsService.getById(withdraw.getPoolsId());
		if(pools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(CollectionUtils.isNotEmpty(param.getPoolsIds()) && ( pools.getCreateUserId() != getUser().getUserId() && pools.getOwnerId()!= getUser().getUserId())  ) {
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
		withDrawService.update(new LambdaUpdateWrapper<WithDrawEntity>()
				.set(WithDrawEntity::getStatus, WithdrawStatusEnum.Approvaled.getCode())
				.set(WithDrawEntity::getRemark, null)
				.eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Error.getCode())
				.eq(WithDrawEntity::getId, param.getId()));
		log.info("钱包:{} 提现被恢复. 提现金额:{}",withdraw.getWallet(),withdraw.getUsdc());
		return R.ok();
	}	
	
	
}
