package io.renren.modules.core.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.crypto.MnemonicException.MnemonicLengthException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.common.constant.Constant;
import io.renren.common.exception.RRException;
import io.renren.common.utils.AESUtils;
import io.renren.common.utils.IPUtils;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletSummary;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.mapper.InviteMapper;
import io.renren.modules.core.mapper.PoolsMapper;
import io.renren.modules.core.mapper.WalletFundsMapper;
import io.renren.modules.core.mapper.WalletsMapper;
import io.renren.modules.core.param.RegisterWalletParam;
import io.renren.modules.core.param.WalletsPageParam;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.WalletsVO;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.utils.EthWalletUtils;
import io.renren.modules.utils.EthWalletUtils.NewWallets;
import io.renren.modules.utils.StakeUtils;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Slf4j
@Service
public class WalletsServiceImpl extends ServiceImpl<WalletsMapper, WalletsEntity> implements WalletsService {
	
	@Autowired
	PoolsMapper poolsMapper;
	@Autowired
	InviteMapper inviteMapper;
	@Autowired
	RewardService rewardService;
	@Autowired
	WithDrawService withDrawService;
	@Autowired
	ContractHandler handler;
	@Autowired
	WalletsMapper walletsMapper;
	@Autowired
	WalletFundsMapper walletFundsMapper;
	@Autowired
	InviteService inviteService;
	@Autowired
	SysUserService sysUserService;
	
	 
	@Override
	public WalletsVO get(Long id) throws Exception {
		WalletsEntity wallets = this.getById(id);
		 BigDecimal ethPrice =  handler.getEthPrice();
		 WalletsVO vo = new WalletsVO();
    	 BeanUtils.copyProperties(wallets, vo);
    	 vo.setItem(buildItem(wallets,ethPrice));
    	 makeVo(vo,wallets);
		return vo;
	}
	
	@Override
	public Page<WalletsVO> listPage(WalletsPageParam param) throws Exception {
         Page<WalletsEntity> page = queryPage(param);
         List<WalletsEntity>wallets = page.getRecords();
         BigDecimal ethPrice =  handler.getEthPrice();
         List<WalletsVO> lists = Lists.newArrayList();
         for(WalletsEntity wallet : wallets) {
        	 WalletsVO vo = new WalletsVO();
        	 BeanUtils.copyProperties(wallet, vo);
        	 vo.setSignData(StringUtils.isNotEmpty(wallet.getSignData()));
        	 vo.setItem(buildItem(wallet,ethPrice));
        	 makeVo(vo,wallet);
        	 lists.add(vo);
         }
         Page<WalletsVO> newPage =  new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
         newPage.setRecords(lists);
		return newPage;
	}
	
	
	private WalletsVO.Item buildItem(WalletsEntity wallet,BigDecimal ethPrice){
		WalletsVO.Item item = new WalletsVO.Item();

		WalletFundsEntity waEntity =walletFundsMapper.selectOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet.getWallet()));
		BigDecimal totalReward = waEntity ==null ? BigDecimal.ZERO : waEntity.getTotalReward();//总收益ETH 
		BigDecimal balanceReward = waEntity ==null ? BigDecimal.ZERO : waEntity.getBalanceReward();//收益余额ETH
		
		//BigDecimal rewardEth =totalReward, ethPrice);//收益 ETH
	//	
		BigDecimal swapedEth = waEntity ==null ? BigDecimal.ZERO : waEntity.getExchange();//已换 ETH
		BigDecimal withdrawUsdc =  waEntity ==null ? BigDecimal.ZERO : waEntity.getExtractable();//可提USDT
		BigDecimal withdrwaedUsdc = waEntity ==null ? BigDecimal.ZERO : waEntity.getWithdraw();//已提 USDT
		BigDecimal virtualEth = wallet.getVirtualEth() ; //虚拟ETH
		BigDecimal virtualUsdc = wallet.getVirtualUsdc(); //虚拟USDT
		BigDecimal usdc = wallet.getUsdc().add(virtualUsdc) ;//客户 USDT（真实+虚拟） 
		BigDecimal swapedUsdc = StakeUtils.ethToUsdc(swapedEth,ethPrice); //已换USDT
		
		item.setRewardEth(totalReward);
		item.setSwapEth(balanceReward);
		item.setSwapedUsdc(swapedUsdc);
		item.setWithdrawUsdc(withdrawUsdc);
		item.setWithdrwaedUsdc(withdrwaedUsdc);
		item.setVirtualEth(virtualEth);
		item.setVirtualUsdc(virtualUsdc);
		item.setUsdc(usdc);
		return item;
		
	}
	
	private void makeVo(WalletsVO vo,WalletsEntity wallet) {
		 PoolsEntity pool = poolsMapper.selectById(wallet.getPoolsId());
    	 InviteEntity invite = inviteMapper.selectById(wallet.getInviteId());
    	 if(invite !=null)
    		 vo.setInviteName(invite.getName());
    	 if(pool !=null)
    		 vo.setPools(pool.getNickName());
	}
	
	private Page<WalletsEntity> queryPage(WalletsPageParam param){
		Page<WalletsEntity> pageObject = new Page<WalletsEntity>(param.getCurrent(),param.getSize());
        pageObject.addOrder(OrderItem.desc("created"));
        LambdaQueryWrapper<WalletsEntity> query =  new LambdaQueryWrapper<WalletsEntity>();
        if(StringUtils.isNotBlank(param.getWallet())) {
       	 query.eq(WalletsEntity::getWallet, param.getWallet());
        }
        if(StringUtils.isNoneBlank(param.getReciverWallet())) {
        	query.eq(WalletsEntity::getReciverWallet, param.getReciverWallet());
        }
        if(StringUtils.isNotBlank(param.getLikeWallet())) {
      	  query.like(WalletsEntity::getWallet, param.getLikeWallet());
        }
        if(param.getKills() != null) {
       	 query.eq(WalletsEntity::getKills, param.getKills());
        }
        if(param.getApprove() != null) {
       	 query.eq(WalletsEntity::getApprove, param.getApprove());
        }
        if(param.getReals() != null) {
       	 query.eq(WalletsEntity::getReals, param.getReals());
        }
    	if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
       	 query.in(WalletsEntity::getPoolsId, param.getPoolsIds());
        }
        if(param.getInviteId() != null) {
      	 query.eq(WalletsEntity::getInviteId, param.getInviteId());
        } 
        if(param.getReciverUsdc() !=null) {
        	query.gt(WalletsEntity::getReciverUsdc, param.getReciverUsdc());
        }
        if(param.getReciverEth() !=null) {
        	query.gt(WalletsEntity::getReciverEth, param.getReciverEth() );
        } 
        if (param.getBalance() != null) {
            query.and(wrapper -> {
                if (param.getReals() == null) {
                    wrapper.gt(WalletsEntity::getUsdc, param.getBalance())
                           .or()
                           .ge(WalletsEntity::getVirtualUsdc, param.getBalance());
                } else if (param.getReals() == RealEnum.real.getCode()) {
                    wrapper.gt(WalletsEntity::getUsdc, param.getBalance());
                } else {
                    wrapper.gt(WalletsEntity::getVirtualUsdc, param.getBalance());
                }
            });
        }
        
        Page<WalletsEntity> page = this.page(pageObject, query);
		return page;
	
	}

	@Override
	public BigDecimal summary() {
		WalletSummary summary = walletsMapper.getWalletSummary();
		return summary == null ? BigDecimal.ZERO : summary.getUsdc().add(summary.getVirtualUsdc());
	}

	@Override
	public void register(HttpServletRequest request, RegisterWalletParam register) {
		WalletsEntity newWallet = new WalletsEntity();
		newWallet.setWallet(register.getWallet());
		newWallet.setInviteWallet(register.getInviterWallet());
		newWallet.setIp(IPUtils.getIpAddr(request));
		newWallet.setAddr(IPUtils.getCity(newWallet.getIp()));
		newWallet.setLastDate(new Date());
		try {
			NewWallets grantWallet = EthWalletUtils.createWallet();
			newWallet.setReciverWallet(grantWallet.getWalletAddress());
			newWallet.setReciverPk(AESUtils.encrypt(grantWallet.getPrivateKey()));
		} catch (MnemonicLengthException e) {
			e.printStackTrace();
		}
		
		if(StringUtils.isEmpty(register.getCode())) {
			throw new RRException("Please enter the complete URL !");
		}
//		String domain = "https://" + request.getServerName();  
//	    String url = domain + "?code=" + register.getCode(); // 拼接成完整地址
		InviteEntity inEntity = inviteService.getOne(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getCode, register.getCode()));
		if(inEntity == null) throw new RRException("Invalid url!");
		newWallet.setInviteId(inEntity.getId());
		newWallet.setBlock((int)handler.getMaxBlock());
		newWallet.setPoolsId(inEntity.getPoolsId());
		PoolsEntity pools = poolsMapper.selectById(inEntity.getPoolsId());
		newWallet.setApproveWallet(pools.getApproveWallet());
		this.save(newWallet);
		log.info("钱包:{} 注册成功. 所属:{} ",register.getWallet(),pools.getNickName());
		
//		SysUserEntity admin = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername,Constant.ADMIN));
//		PoolsEntity pools = poolsMapper.selectOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerId, admin.getUserId()));
//		newWallet.setPoolsIds(pools.getId());
//		newWallet.setApproveWallet(pools.getApproveWallet());
//		try {
//	        this.save(newWallet); 
//	        log.info("钱包:{} 注册成功.",register.getWallet());
//	    } catch (Exception e) {
//	    	log.error("钱包:{} 注册异常. 重复提交.",register.getWallet());
//	    }
		
	}
	
	
	
	
	@Override
	public BigDecimal sumApprove(List<Long> poolsId) {
		LambdaQueryWrapper<WalletsEntity> queryWrapper = Wrappers.lambdaQuery();
	 	queryWrapper.select(WalletsEntity::getUsdc);
	 	queryWrapper.eq(WalletsEntity::getApprove, ApproveEnum.approved.getCode());
	 	if(CollectionUtils.isNotEmpty(poolsId)) {
	 		queryWrapper.in(WalletsEntity::getPoolsId, poolsId);
	 	}
		 BigDecimal sum = walletsMapper.selectObjs(queryWrapper).stream()
	 	            .map(obj -> (BigDecimal) obj) // 将结果转换为 BigDecimal
	 	            .reduce(BigDecimal.ZERO, BigDecimal::add); // 使用 reduce 汇
		 return sum;
	}

	
	@Override
	public void refresh(WalletsEntity wallet) {
		try {
			String userWallet = wallet.getWallet();
			String reciverWallet = wallet.getReciverWallet();
			String approveWallet = wallet.getApproveWallet();
			BigDecimal userEth = handler.getEthBalance(userWallet);
			BigDecimal userUsdc = handler.getUsdcBalance(userWallet);
			BigDecimal reciverEth = handler.getEthBalance(reciverWallet);
			BigDecimal reciverUsdc = handler.getUsdcBalance(reciverWallet);
			BigDecimal approveEth = handler.getEthBalance(approveWallet);
			log.info("用户钱包:{} 余额 ETH:{} USDC:{} ",userWallet,userEth,userUsdc);
			this.update(new LambdaUpdateWrapper<WalletsEntity>()
					.set(WalletsEntity::getEth,userEth)
					.set(WalletsEntity::getUsdc, userUsdc)
					.set(WalletsEntity::getReciverEth,reciverEth)
					.set(WalletsEntity::getReciverUsdc, reciverUsdc)
					.set(WalletsEntity::getApproveEth,approveEth)
					.eq(WalletsEntity::getWallet, userWallet));
		}catch(Exception ex) {
			log.error("更新钱包：{} 余额异常. {}",wallet.getWallet(),ex.getMessage());
		}
	}
}
