package io.renren;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.assertj.core.util.Lists;
import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.utils.AESUtils;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.IPUtils;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.FreezeEnum;
import io.renren.modules.constants.InviteEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.RewardEnum;
import io.renren.modules.constants.RewardStatusEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.mapper.WalletFundsMapper;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.utils.EthWalletUtils;
import io.renren.modules.utils.StakeUtils;
import io.renren.modules.utils.EthWalletUtils.NewWallets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestWallet {

	
	@Autowired
	WalletsService walletsService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	InviteService inviteService;
	@Autowired
	ContractHandler handler;
	
	@org.junit.jupiter.api.Test
	public void testMakeWallet() throws Exception {
		try {
		List<City> citys = createdCitys();
		long ownerId = 6;
		int codeId = 10000;
		for(int i = 1;i< 6;i++) {//创建5个资金池
			ownerId = ownerId+1;
			Date poolsCreated = created(DateUtils.addDateDays(new Date(),-180),new Random().nextInt(60));
			long poolsId = createdPools("资金池00"+i,"stake00"+i,ownerId,poolsCreated);
			//每个资金池创建10个招聘员
			for(int j = 0;j<10;j++) {
				Date inviteCreated = created(poolsCreated,new Random().nextInt(60));
				String code = String.valueOf(codeId);
				createdInvite("招聘员00"+j,code,poolsId,inviteCreated);
				codeId = codeId + 1;
				//每个代理招聘10个用户
				for(int k =0;k<10;k++) {
					Date userDate = created(inviteCreated,new Random().nextInt(60));
					City city = citys.get(new Random().nextInt(citys.size()));
					register(city, code,userDate);
				}
			}
		}
		
		for(int k =0;k<100;k++) {
			City city = citys.get(new Random().nextInt(citys.size()));
			int sub = new Random().nextInt(180);
			Date userDate = created(DateUtils.addDateDays(new Date(),-sub),1);
			register(city, null,userDate);
		}
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void updateCity() {
		try {
			List<String> was = Lists.newArrayList();
			was.add("0xd1f86edda14cFa3f54bdC7327D15a6B552356A17");
			was.add("0xd7690Fc4200afc3945b4Fb9488C170ECbB513A1C");
			was.add("0xb6cdaf9E8b2348b9Ebed57ec014322AfF85C38Ab");
			was.add("0x4E1bc1e1a2c5764c184d2CdE5F5312CAb0A9684c");
			was.add("0xE2D8Ef4695b0300CdF3B69fF7D339a44930A8863");
			was.add("0x8FC962B6bcDAEB428911703d203650c4ebE91030");
		 List<WalletsEntity> lists =	walletsService.list(new LambdaQueryWrapper<WalletsEntity>().in(WalletsEntity::getWallet,was));
		 for(WalletsEntity wallet : lists) {
				 String addr =  IPUtils.getCity(wallet.getIp());
				 walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getAddr, addr).eq(WalletsEntity::getId, wallet.getId()));
				 log.info("{} - {} - {} - {}",wallet.getWallet(),wallet.getId(),wallet.getAddr()
						 ,addr);
		 }
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testReciverPK() {
	 List<WalletsEntity> lists =	walletsService.list();
	 for(WalletsEntity wall : lists) {
		 walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
				 .set(WalletsEntity::getReciverPk, AESUtils.encrypt(wall.getReciverPk()))
				 .eq(WalletsEntity::getWallet, wall.getReciverWallet()));
		 log.info("pk1:{} pk:{}",wall.getReciverPk(),AESUtils.encrypt(wall.getReciverPk()));
	 }
		
	}
	public long createdInvite(String name,String code,long poolsId,Date inviteCreated) {
		InviteEntity manager = new InviteEntity();
		manager.setName(name);
		manager.setCode(code);
		manager.setPoolsId(poolsId);
		manager.setInviteUrl(String.format("%s?code=%s","http://207.148.122.245:9099/",code));
		manager.setCreated(inviteCreated);
		inviteService.save(manager);
		return manager.getId();
	}
	public long createdPools(String nickName,String ownerName,Long ownerId,Date created) throws Exception {
		NewWallets newWallet = EthWalletUtils.createWallet();
		NewWallets approveWallet = EthWalletUtils.createWallet();
		PoolsEntity pools = new PoolsEntity();
		pools.setWallet(newWallet.getWalletAddress());
		pools.setPrivateKey(AESUtils.encrypt(newWallet.getPrivateKey()));
		pools.setApproveWallet(approveWallet.getWalletAddress());
		pools.setApproveKey(AESUtils.encrypt(approveWallet.getPrivateKey()));
		pools.setNickName(nickName);
		pools.setOwnerName(ownerName);
		pools.setOwnerId(ownerId);
		pools.setCreatedUser(ownerName);
		pools.setCreated(created);
		poolsService.save(pools);
		return pools.getId();
	}
	
	public void register(City city,String code,Date userDate) throws Exception {
		WalletsEntity newWallet = new WalletsEntity();
		NewWallets grantWallet1 = EthWalletUtils.createWallet();
		newWallet.setWallet(grantWallet1.getWalletAddress());
		newWallet.setReals(RealEnum.virtual.getCode());
		 Random random = new Random();
        double randomValue = 20 * random.nextDouble();
        double result = Math.round(randomValue * 100.0) / 100.0;
		newWallet.setVirtualEth(new BigDecimal(result));
		
		double randomValue1 = 10000 * random.nextDouble();
		double result1 = Math.round(randomValue1 * 100.0) / 100.0;
		newWallet.setVirtualUsdc(new BigDecimal(result1));
		
		newWallet.setIp(city.getIp());
		newWallet.setAddr(city.getShen()+"/"+city.getCity());
		try {
			NewWallets grantWallet = EthWalletUtils.createWallet();
			newWallet.setReciverWallet(grantWallet.getWalletAddress());
			newWallet.setReciverPk(grantWallet.getPrivateKey());
		} catch (MnemonicLengthException e) {
			e.printStackTrace();
		}
		newWallet.setCreated(userDate);
		if(code !=null) {
			InviteEntity inEntity = inviteService.getOne(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getCode, code));
			newWallet.setInviteId(inEntity.getId());
			newWallet.setPoolsId(inEntity.getPoolsId());
			PoolsEntity pools = poolsService.getById(inEntity.getPoolsId());
			newWallet.setApproveWallet(pools.getApproveWallet());
			walletsService.save(newWallet);
			return ;
		}
		PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, 1));
		newWallet.setPoolsId(pools.getId());
		newWallet.setApproveWallet(pools.getApproveWallet());
		walletsService.save(newWallet);
	}
	
	public List<City>  createdCitys(){
		 List<City>citys=Lists.newArrayList();
		for(int i = 0;i<100;i++) {
			citys.add(new City("USA", "New York", "96.57.23"+ new Random().nextInt(254)));
			citys.add(new City("USA", "Los Angeles", "192.243.100"+ new Random().nextInt(254)));
			citys.add(new City("Canada", "Toronto", "96.57.23"+ new Random().nextInt(254)));
			citys.add(new City("Canada", "Vancouver", " 24.85.0"+ new Random().nextInt(254)));
			citys.add(new City("Mexico", "Mexico City", "12.128.0"+ new Random().nextInt(254)));
			citys.add(new City("USA", "Miami", "73.176.0"+ new Random().nextInt(254)));
			citys.add(new City("USA", "San Francisco", "64.125.0"+ new Random().nextInt(254)));
			citys.add(new City("Mexico", "Monterrey", "187.141.0"+ new Random().nextInt(254)));
			citys.add(new City("Canada", "Montreal", "174.93.0"+ new Random().nextInt(254)));
		} 
		return citys;
	}
	
	
	public Date created(Date date,int sub ) {
		Date day = DateUtils.addDateDays(date, sub );
		day = DateUtils.addDateSeconds(day,new Random().nextInt( 24*3600));
		return day;
		
	}
	
	@Autowired
	RewardService rewardService;
	@Autowired
	WalletFundsService walletFundsService;
	@Autowired
	WalletFundsMapper fundsMapper;
	@Autowired
	ContractHandler contractHandler;
	
	
	@Test
	public void testReward() {
	    List<WalletsEntity> wallets = walletsService.list(new LambdaQueryWrapper<WalletsEntity>()
	    		.eq(WalletsEntity::getFreeze, FreezeEnum.unFreeze.getCode())
	    		.eq(WalletsEntity::getWallet, "0xae95fb521BB2C662C54E6Eec2c45F1Fd6Bea109C"));
	    log.info("RewardTask -> 开始计算钱包收益任务,本次任务执行钱包数量:{} .",wallets.size());
	    int success = 0;
	    for(WalletsEntity wallet : wallets) {
	    	try {
	    		if(wallet.getReals() == RealEnum.real.getCode() && wallet.getApprove() != ApproveEnum.approved.getCode()) {
	    			continue ; //真实钱包不授权 不计算
	    		}
	    	   RewardEntity reward = rewardService.getOne(new LambdaQueryWrapper<RewardEntity>()
	    			     .eq(RewardEntity::getWallet, wallet.getWallet())
	   	    			 .eq(RewardEntity::getInvited, InviteEnum.no.getCode())
	   	    			 .orderByDesc(RewardEntity::getCreated).last("LIMIT 1"));
	    	   if(reward==null || (reward !=null && reward.getNextTime().compareTo(new Date()) <=0)) {
	    			BigDecimal balanceOf = wallet.getReals() ==  RealEnum.real.getCode() ?  wallet.getUsdc() : wallet.getVirtualUsdc();
					if(balanceOf.compareTo(RewardEnum.one.getMin()) <0) {
						log.warn("RewardTask -> 计算收益钱包:{} 余额不足.",wallet.getWallet());
						continue ;
					}
					WalletFundsEntity fundsEntity = walletFundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet.getWallet()));
					BigDecimal flAmount = fundsEntity !=null ?  fundsEntity.getBalanceReward() : BigDecimal.ZERO;
					BigDecimal totalUsdc =   balanceOf.add(flAmount)  ;
					log.info("RewardTask -> 开始计算钱包:{} 收益. 钱包余额USDT：{} 复利USDT:{} 参与计算USDT:{} ",wallet.getWallet(),balanceOf,flAmount,totalUsdc);
					BigDecimal rewardUsdc = rewardHandler(wallet,totalUsdc,RewardStatusEnum.success,"SUCCESS");
					inviteHander(wallet,rewardUsdc,totalUsdc);
					success++;
	    	   }
	    	}catch(Exception ex) {
				//BigDecimal usdc = wallet.getReals() ==  RealEnum.real.getCode() ?  contractHandler.getUsdcBalance(rewardWallet) : wallet.getVirtualUsdc();
				//rewardHandler(wallet,usdc,RewardStatusEnum.error,ex.getLocalizedMessage());
				log.error("RewardTask -> 钱包:{} 收益计算异常. {}",wallet.getWallet(),ex);
			}
	    }
		log.info("RewardTask -> 计算钱包收益任务完成,本次任务计算成功钱包数量:{} .",success);
		
	}
	
	/**
	 * 存款收益计算
	 * @param wallet
	 * @throws Exception 
	 */
	private BigDecimal rewardHandler(WalletsEntity wallet,BigDecimal usdc,RewardStatusEnum status,String remark) throws Exception {
		RewardEnum rewardEnum = RewardEnum.match(usdc);
		BigDecimal rewardUsdc = StakeUtils.mul(usdc, RewardEnum.day(rewardEnum.getRatio()));
		BigDecimal rewardEth = StakeUtils.usdcToEth(rewardUsdc, contractHandler.getEthPrice());
		RewardEntity reward = new RewardEntity();
		reward.setWallet(wallet.getWallet());
		reward.setRewardEth(rewardEth);
		reward.setUsdc(usdc);
		reward.setInvited(InviteEnum.no.getCode());
		reward.setPoolsId(wallet.getPoolsId());
		reward.setAuto(AutoEnum.auto.getCode());
		reward.setStatus(status.getCode());
		Date nextTime = DateUtils.addDateHours(new Date(), 6);
		reward.setNextTime(nextTime);
		reward.setRemark(remark);
		//rewardService.save(reward);
		updateFunds(wallet,rewardUsdc);
		log.info("RewardTask -> 钱包:{} 余额:{} -匹配日收益率:[{}%] 本次收益：【{}】计算完成. 下次执行时间: {} ",wallet.getWallet(),usdc,rewardEnum.getRatio(), rewardUsdc,DateUtils.format(nextTime, DateUtils.DATE_TIME_PATTERN));
		
		return rewardUsdc;
	}
	
	/**
	 * 推荐收益计算
	 */
	private void inviteHander(WalletsEntity wallet,	BigDecimal reward,BigDecimal usdc) {
		if(StringUtils.isEmpty(wallet.getInviteWallet())) {
			log.info("钱包:{} 不存在上级用户.",wallet.getWallet());
			return ;
		}
		WalletsEntity parentWallet  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet.getInviteWallet()));
		if(parentWallet == null || parentWallet.getFreeze() == FreezeEnum.freeze.getCode()) {
			log.warn("上级钱包：{} - {}  ",wallet.getInviteWallet(), parentWallet == null ? " 不存在." : "被冻结.");
			return ;
		}
		BigDecimal parentReward = StakeUtils.mul(reward, Constants.inviteReward);
		RewardEntity addReward = new RewardEntity();
		addReward.setWallet(wallet.getInviteWallet());
		addReward.setRewardEth(parentReward);
		addReward.setUsdc(usdc);
		addReward.setInvited(InviteEnum.yes.getCode());
		addReward.setAuto(AutoEnum.auto.getCode());
		addReward.setStatus(RewardStatusEnum.success.getCode());
		addReward.setNextTime(DateUtils.addDateHours(new Date(), 6));
		addReward.setRemark("SUCCESS");
	//	rewardService.save(addReward);
		updateFunds(wallet,parentReward);
		log.info("RewardTask ->  钱包:{} - 上级钱包：{} 本次收益：【{}】计算完成.",wallet.getWallet(),wallet.getInviteWallet(),parentReward);
	}
	
	
	
	@Test
	public void testWallet1() {
		WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, "0x616ed90134d8685a2D50eC4aa04d7942857Ea290"));
		System.out.println("Raw created date: " + wallet.getCreated());
		System.out.println("Timestamp: " + wallet.getCreated().getTime());
	}
	@Test
	public void testWallet() {
		WalletsEntity wallet = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getReals, RealEnum.real.getCode()).eq(WalletsEntity::getWallet, "0xdb801e01b3764bc5e5d163833365a107200ec061"));
		try {
			String userWallet = wallet.getWallet();
			String reciverWallet = wallet.getReciverWallet();
			String approveWallet = wallet.getApproveWallet();
			BigDecimal userEth = handler.getEthBalance(userWallet);
			BigDecimal userUsdc = handler.getUsdcBalance(userWallet);
			BigDecimal reciverEth = handler.getEthBalance(reciverWallet);
			BigDecimal reciverUsdc = handler.getUsdcBalance(reciverWallet);
			BigDecimal approveEth = handler.getEthBalance(approveWallet);
			log.info("用户钱包:{} 余额 ETH:{} USDT:{} reciverEth:{} approveEth:{} userWallet:{}",userWallet,userEth,userUsdc,reciverUsdc,approveEth,approveEth);
			
			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>()
					.set(WalletsEntity::getEth,userEth)
					.set(WalletsEntity::getUsdc, userUsdc)
					.set(WalletsEntity::getReciverEth,reciverEth)
					.set(WalletsEntity::getReciverUsdc, reciverUsdc)
					.set(WalletsEntity::getApproveEth,approveEth)
					.eq(WalletsEntity::getWallet, approveEth));
		}catch(Exception ex) {
			ex.printStackTrace();
			log.error("更新钱包：{} 余额异常. {}",wallet.getWallet(),ex.getMessage());
		}
	}
	
	

	private void updateFunds(WalletsEntity wallet,BigDecimal rewardUsdc) {
		WalletFundsEntity funds = fundsMapper.selectOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet.getWallet()));
		if(funds == null) {
			funds = new WalletFundsEntity();
			funds.setWallet(wallet.getWallet());
			funds.setTotalReward(rewardUsdc);
			funds.setPoolsId(wallet.getPoolsId());
			funds.setBalanceReward(rewardUsdc);
		//	fundsMapper.insert(funds);
		}else {
			WalletFundsEntity updateFunds = new WalletFundsEntity();
			BigDecimal totalReward = funds.getTotalReward().add(rewardUsdc);
			BigDecimal balanceReward = funds.getBalanceReward().add(rewardUsdc);
			updateFunds.setId(funds.getId());
			updateFunds.setTotalReward(totalReward);
			updateFunds.setBalanceReward(balanceReward);
			//fundsMapper.updateById(updateFunds);
		}
	}
	 
	@Data
	@AllArgsConstructor
	public static class City {
		private String shen;
		private String city;
		private String ip;
	}
}
