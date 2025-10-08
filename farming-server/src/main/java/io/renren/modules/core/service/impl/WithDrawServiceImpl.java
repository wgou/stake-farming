package io.renren.modules.core.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.common.exception.RRException;
import io.renren.common.utils.RedisUtils;
import io.renren.modules.constants.FreezeEnum;
import io.renren.modules.constants.RealEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.context.WalletConext;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.WalletFundsEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.mapper.InviteMapper;
import io.renren.modules.core.mapper.PoolsMapper;
import io.renren.modules.core.mapper.WithDrawMapper;
import io.renren.modules.core.param.WithDrawIndexParam;
import io.renren.modules.core.param.WithDrawParam;
import io.renren.modules.core.service.AsyncTelegramNotificationService;
import io.renren.modules.core.service.WalletFundsService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.WithDrawVO;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Slf4j
@Service
public class WithDrawServiceImpl extends ServiceImpl<WithDrawMapper, WithDrawEntity> implements WithDrawService {
	
	@Autowired
	InviteMapper inviteMapper;
	@Autowired
	PoolsMapper poolsMapper;
	@Autowired
	WalletsService walletsService;
	@Autowired
	WalletFundsService fundsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	AsyncTelegramNotificationService asyncTelegramService;
	@Autowired
	RedisUtils redisUtils;
	
	@Override
	public BigDecimal sum(WithDrawParam param) {
		List<WithDrawEntity> lists = this.list(buildQuery(param));
		BigDecimal totalUsdc =  lists==null?BigDecimal.ZERO : lists.stream()
				.filter(w -> w.getStatus() == WithdrawStatusEnum.Success.getCode())
				.filter(w -> StringUtils.isNotBlank(w.getHash()))
				.map(WithDrawEntity::getUsdc)
				.reduce(BigDecimal.ZERO,BigDecimal::add);
		return totalUsdc;
	}
	
	@Override
	public Page<WithDrawVO> pageList(WithDrawParam param) {
	
		Page<WithDrawEntity> pageObject = new Page<WithDrawEntity>(param.getCurrent(),param.getSize());
        pageObject.addOrder(OrderItem.desc("created"));
        Page<WithDrawEntity> page = this.page(pageObject, buildQuery(param));
        
        List<WithDrawEntity>withdraws = page.getRecords();
        
        List<WithDrawVO> lists = Lists.newArrayList();
        for(WithDrawEntity withdraw : withdraws) {
        	 WithDrawVO vo = new WithDrawVO();
	       	 BeanUtils.copyProperties(withdraw, vo);
	       	 vo.setWithdrawUsdc(withdraw.getUsdc());
	       	 makeVo(vo,withdraw);
	       	 lists.add(vo);
        }
        Page<WithDrawVO> newPage =  new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        newPage.setRecords(lists);
		return newPage;
	}
	
	@Override
	public List<WithDrawEntity> query(WithDrawParam param){
		return this.list(buildQuery(param));
	
	}
	
	private void makeVo(WithDrawVO vo,WithDrawEntity wallet) {
		 PoolsEntity pool = poolsMapper.selectById(wallet.getPoolsId());
	 	 InviteEntity invite = inviteMapper.selectById(wallet.getInviteId());
	   	 if(invite !=null)
	   		 vo.setInviteName(invite.getName());
	   	 if(pool !=null)
	   		 vo.setPools(pool.getNickName());
		}
		
	private  LambdaQueryWrapper<WithDrawEntity> buildQuery(WithDrawParam param){
        LambdaQueryWrapper<WithDrawEntity> query =  new LambdaQueryWrapper<WithDrawEntity>();
        query.orderByDesc(WithDrawEntity::getCreated);
        if(StringUtils.isNotBlank(param.getWallet())) {
       	  query.eq(WithDrawEntity::getWallet, param.getWallet());
        }
        if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
       	 query.in(WithDrawEntity::getPoolsId, param.getPoolsIds());
        }
        if(param.getInviteId() != null) {
          	 query.eq(WithDrawEntity::getInviteId, param.getInviteId());
         }
        if(param.getStatus() !=null) {
        	 query.eq(WithDrawEntity::getStatus, param.getStatus());
        }
        if(param.getReal() !=null) {
       	 	query.eq(WithDrawEntity::getReals, param.getReal());
        }
        if(param.getStart() !=null) {
        	query.ge(WithDrawEntity::getCreated, param.getStart());
        }
        if(param.getEnd() !=null) {
        	query.le(WithDrawEntity::getCreated, param.getEnd());
        } 
        return query;
	}
	@Override
	public void withDraw(WithDrawIndexParam param) throws Exception {
		String wallet = WalletConext.getWallet();
		String lockKey = "withdraw:" + wallet;
		try {
			if(!redisUtils.tryGlobalLock(lockKey, 30)) throw new RRException("Frequent operations");
			WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, wallet));
			if(walletEntity == null) throw new RRException("Invalid wallet");
			if(walletEntity.getFreeze() == FreezeEnum.freeze.getCode()) throw new RRException("Account frozen ");
			WalletFundsEntity fundsEntity = fundsService.getOne(new LambdaQueryWrapper<WalletFundsEntity>().eq(WalletFundsEntity::getWallet, wallet));
			if(fundsEntity == null) throw new RRException("No available balance");
			if(param.getUsdc().compareTo(BigDecimal.ZERO) <=0  || fundsEntity.getExtractable().compareTo(BigDecimal.ZERO) <=0  ||  param.getUsdc().compareTo(fundsEntity.getExtractable()) > 0) {
				 throw new RRException("Insufficient withdrawable balance");
			}
			
			BigDecimal newExtractable = fundsEntity.getExtractable().subtract(param.getUsdc());
			BigDecimal newWithdraw = fundsEntity.getWithdraw().add(param.getUsdc());
			
			fundsService.update(new LambdaUpdateWrapper<WalletFundsEntity>()
					.set(WalletFundsEntity::getExtractable, newExtractable)
					.set(WalletFundsEntity::getWithdraw, newWithdraw)
					.eq(WalletFundsEntity::getWallet, wallet));
			
			WithDrawEntity drawEntity = new WithDrawEntity();
			drawEntity.setWallet(wallet);
			drawEntity.setUsdc(param.getUsdc());
			drawEntity.setPoolsId(walletEntity.getPoolsId());
			drawEntity.setInviteId(walletEntity.getInviteId());
			drawEntity.setReals(walletEntity.getReals());
			if(walletEntity.getReals() == RealEnum.virtual.getCode()) {
				drawEntity.setBalance(walletEntity.getVirtualUsdc());
				drawEntity.setStatus(WithdrawStatusEnum.Success.getCode());
				BigDecimal newUsdc = walletEntity.getVirtualUsdc().add(param.getUsdc());
				walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getVirtualUsdc, newUsdc).eq(WalletsEntity::getWallet, wallet));
				log.info("虚拟钱包:{} 虚拟USDC 余额【{}】增加成功.",wallet,newUsdc);
			}else {
				drawEntity.setBalance(walletEntity.getUsdc());
				drawEntity.setStatus(WithdrawStatusEnum.Approvaling.getCode());
				//提现审批通知
				sendWithdrawAutoApprovalNotification(walletEntity,param.getUsdc());
			}
			this.save(drawEntity);
			log.info("钱包:{} 提现申请成功. 本次提现USDT：{}",wallet,param.getUsdc());
		}finally {
			redisUtils.releaseGlobalLock(lockKey);
		}
	} 
	
	@Override
	public BigDecimal sum(String wallet) {
		List<WithDrawEntity> lists = this.list(new LambdaQueryWrapper<WithDrawEntity>().eq(WithDrawEntity::getWallet, wallet).eq(WithDrawEntity::getStatus, WithdrawStatusEnum.Success.getCode()));
		return lists.stream().map(WithDrawEntity::getUsdc).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	/**
	 * 发送提现审批通知
	 */
	private void sendWithdrawAutoApprovalNotification(WalletsEntity wallet,  BigDecimal totalAmount) {
		try {
			StringBuilder message = new StringBuilder();
			message.append("🎯 您有一笔提现订单待审批,请及时处理！\n")
				   .append("💳 钱包地址: ").append(wallet.getWallet()).append("\n")
				   .append("💰 总提现金额: ").append(totalAmount.toPlainString()).append(" USDC\n")
				   .append("✅ 状态:待审批");
			
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message.toString());
			log.info("提现审批通知已加入队列 - 钱包: {},  总金额: {} USDC", 
				wallet.getWallet(), totalAmount);
			
		} catch (Exception e) {
			log.error("发送提现审批通知失败 - 钱包: {}, 错误: {}", wallet.getWallet(), e.getMessage());
		}
	}
}
