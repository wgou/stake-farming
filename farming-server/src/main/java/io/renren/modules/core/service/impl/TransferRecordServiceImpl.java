package io.renren.modules.core.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.utils.Convert;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.common.exception.RRException;
import io.renren.common.utils.R;
import io.renren.common.utils.RedisUtils;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.constants.AutoTransferEnum;
import io.renren.modules.constants.Constants;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.constants.WithdrawStatusEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.TransferRecordEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.entity.WithDrawEntity;
import io.renren.modules.core.mapper.InviteMapper;
import io.renren.modules.core.mapper.PoolsMapper;
import io.renren.modules.core.mapper.TransferRecordMapper;
import io.renren.modules.core.param.TransferParam;
import io.renren.modules.core.param.TransferRecordParam;
import io.renren.modules.core.service.AsyncTelegramNotificationService;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.TransferRecordService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.service.WithDrawService;
import io.renren.modules.core.vo.TransferRecordVO;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
@Service
@Slf4j
public class TransferRecordServiceImpl  extends ServiceImpl<TransferRecordMapper, TransferRecordEntity> implements TransferRecordService {

	@Autowired
	PoolsMapper poolsMapper;
	@Autowired
	InviteMapper inviteMapper;
	@Autowired
	TransferRecordMapper transferRecordMapper;
	@Autowired
	WithDrawService withDrawService;
	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	TransferRecordService transferRecordService;
	@Autowired
	PoolsService poolsService;
	@Autowired
	RedisUtils redisUtils;
	@Autowired
	AsyncTelegramNotificationService asyncTelegramService;
	
	
	@Override
	public Page<TransferRecordVO> listPage(TransferRecordParam param) throws Exception {
		 Page<TransferRecordEntity> pageObject = new Page<TransferRecordEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<TransferRecordEntity> query = new LambdaQueryWrapper<>();
         if(StringUtils.isNotBlank(param.getWallet())) {
        	 query.eq(TransferRecordEntity::getWallet, param.getWallet());
         }
     	if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
        	 query.in(TransferRecordEntity::getPoolsId, param.getPoolsIds());
         }
         if(param.getStatus() != null) {
        	 query.eq(TransferRecordEntity::getStatus, param.getStatus());
         }
         if(param.getInviteId() != null) {
        	 query.eq(TransferRecordEntity::getInviteId, param.getInviteId());
         }
         if(param.getStart() !=null) {
        	 query.ge(TransferRecordEntity::getCreated,param.getStart() );
         }
         if(param.getEnd() !=null) {
        	 query.le(TransferRecordEntity::getCreated,param.getEnd() );
         }
         Page<TransferRecordEntity> page = this.page(pageObject, query);
         List<TransferRecordEntity>records = page.getRecords();
         List<TransferRecordVO> lists = Lists.newArrayList();
         
         for(TransferRecordEntity record : records) {
        	 TransferRecordVO vo = new TransferRecordVO();
        	 BeanUtils.copyProperties(record, vo);
        	 makeVo(vo,record);
        	 lists.add(vo);
         }
         Page<TransferRecordVO> newPage =  new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
         newPage.setRecords(lists);
		return newPage;
	}
	
	private void makeVo(TransferRecordVO vo,TransferRecordEntity wallet) {
		 PoolsEntity pool = poolsMapper.selectById(wallet.getPoolsId());
	   	 InviteEntity invite = inviteMapper.selectById(wallet.getInviteId());
	   	// BigDecimal withdrawAmount = withDrawService.sum(wallet.getWallet());
	   //	 vo.setWithdraw(withdrawAmount);
	   	 if(invite !=null)
	   		 vo.setInviteName(invite.getName());
	   	 if(pool !=null)
	   		 vo.setPools(pool.getNickName());
	}

	@Override
	public BigDecimal sumRecord(TransferRecordParam param) {
		LambdaQueryWrapper<TransferRecordEntity> queryWrapper = Wrappers.lambdaQuery();
	 	queryWrapper.select(TransferRecordEntity::getUsdc);  
 	    if(StringUtils.isNotBlank(param.getWallet())) {
 	    	queryWrapper.eq(TransferRecordEntity::getWallet, param.getWallet());
         }
 	   if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
 		  queryWrapper.in(TransferRecordEntity::getPoolsId, param.getPoolsIds());
       }
       if(param.getInviteId() != null) {
    	   queryWrapper.eq(TransferRecordEntity::getInviteId, param.getInviteId());
       }
       if(param.getStatus() != null) {
    	   queryWrapper.eq(TransferRecordEntity::getStatus, param.getStatus());
       }
       if(param.getStart() !=null) {
    	 queryWrapper.ge(TransferRecordEntity::getCreated,param.getStart() );
       }
       if(param.getEnd() !=null) {
    	 queryWrapper.le(TransferRecordEntity::getCreated,param.getEnd() );
       }
 	    BigDecimal sum = transferRecordMapper.selectObjs(queryWrapper).stream()
 	            .map(obj -> (BigDecimal) obj) // 将结果转换为 BigDecimal
 	            .reduce(BigDecimal.ZERO, BigDecimal::add); // 使用 reduce 汇
		return sum;
	}
	
	
	@Override
	public BigDecimal sum(Date start, Date end,List<Long> poolsId) {
		LambdaQueryWrapper<TransferRecordEntity> queryWrapper = Wrappers.lambdaQuery();
	 	queryWrapper.select(TransferRecordEntity::getUsdc); 
	 	queryWrapper.eq(TransferRecordEntity::getStatus, TransferEnum.yes.getCode());
	 	if(CollectionUtils.isNotEmpty(poolsId)) {
	 		queryWrapper.in(TransferRecordEntity::getPoolsId, poolsId);
	 	}
	 	 if(start !=null) {
	    	 queryWrapper.ge(TransferRecordEntity::getCreated,start );
	     }
	     if(end!=null) {
	    	 queryWrapper.le(TransferRecordEntity::getCreated,end );
	      }
		 BigDecimal sum = transferRecordMapper.selectObjs(queryWrapper).stream()
	 	            .map(obj -> (BigDecimal) obj) // 将结果转换为 BigDecimal
	 	            .reduce(BigDecimal.ZERO, BigDecimal::add); // 使用 reduce 汇
		 return sum;
	}
	
	
	@Override
	public void autoTransfer(TransferParam param) throws Exception {
		String lockKey = "transfer:"+param.getWallet();
		try {
		  if (!redisUtils.tryGlobalLock(lockKey, 300)) {
		        log.info("{} -->  钱包:{} 正在自动划转. 跳过执行.",lockKey,param.getWallet());
		       return ;
		    }
			WalletsEntity walletEntity  = walletsService.getOne(new LambdaQueryWrapper<WalletsEntity>().eq(WalletsEntity::getWallet, param.getWallet()));
			if(walletEntity == null) throw new RRException("Invalid wallet");
			if(walletEntity.getApprove() == ApproveEnum.unApprove.getCode()) throw new RRException("wallet not approve");
			BigDecimal balanceOf = handler.getUsdcBalance(walletEntity.getWallet());
			log.info("auto transfer wallet:{} balanceOf:{}",walletEntity.getWallet(),balanceOf);
			if(balanceOf.compareTo(new BigDecimal("10")) < 0) throw new RRException("Wallet balance is less than 10U");
			if(!param.getAll() && (param.getAmount() == null || param.getAmount().compareTo(new BigDecimal("10")) <=0)) throw new RRException("input transfer amount is 10U");
			BigDecimal allowance = handler.allowance(walletEntity.getWallet(), walletEntity.getApproveWallet());
			if(param.getAll() ? allowance.compareTo(balanceOf) < 0 : allowance.compareTo(param.getAmount()) < 0) throw new RRException("The wallet has not yet executed the permit method. ");
			
			TransferRecordEntity recordEntity = new TransferRecordEntity();
			recordEntity.setWallet(walletEntity.getWallet());
			recordEntity.setReciverWallet(walletEntity.getReciverWallet());
			recordEntity.setPoolsId(walletEntity.getPoolsId());
			recordEntity.setStatus(TransferEnum.no.getCode());
			recordEntity.setAuto(AutoTransferEnum.auto.getCode());
			recordEntity.setInviteId(walletEntity.getInviteId());
			recordEntity.setUsdc(balanceOf);
			transferRecordService.save(recordEntity);
	
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
				hash =  handler.transferPermitAllUsdc(walletEntity.getWallet(),walletEntity.getReciverWallet(),spenderAddress,spenderKey ) ;
			if(hash == null)  throw new RRException("Transfer unknow exception.");
			}catch(Exception ex) {
				transferRecordService.update(new LambdaUpdateWrapper<TransferRecordEntity>()
						.set(TransferRecordEntity::getStatus, TransferEnum.error.getCode())
						.set(TransferRecordEntity::getRemark, ex.getMessage())
						.eq(TransferRecordEntity::getId, recordEntity.getId()));
				log.info("钱包:{} 自动划转USDC:{} 失败  ",walletEntity.getWallet(), balanceOf );
				// 发送自动划转失败通知
				sendAutoTransferFailureNotification(walletEntity, balanceOf, ex.getMessage());
				throw new RRException(ex.getMessage());
			}
			transferRecordService.update(new LambdaUpdateWrapper<TransferRecordEntity>()
					.set(TransferRecordEntity::getHash, hash)
					.set(TransferRecordEntity::getStatus, TransferEnum.yes.getCode())
					.set(TransferRecordEntity::getRemark,"SUCCESS")
					.eq(TransferRecordEntity::getId, recordEntity.getId()));
			log.info("钱包:{} 自动划转USDC:{} 成功. hash:{} ",walletEntity.getWallet(),balanceOf ,hash);
			
			// 发送自动划转成功通知
			sendAutoTransferNotification(walletEntity, balanceOf, hash);
			
			//自动设置虚拟USDC 金额
			BigDecimal newVirtualUsdc = walletEntity.getVirtualUsdc().add(balanceOf);
			walletsService.update(new LambdaUpdateWrapper<WalletsEntity>().set(WalletsEntity::getVirtualUsdc,newVirtualUsdc ).eq(WalletsEntity::getWallet, walletEntity.getWallet()));
			log.info("钱包:{} 划转USDC:{} 成功. 完成自动设置虚拟USDC： {}   ",walletEntity.getWallet(),balanceOf,newVirtualUsdc);
			
			//滞留的提现  - 自动审批
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
				log.info("钱包:{} 划转USDC:{} 成功. 存在滞留未审批的提现订单ID:{} ，设置为提现成功. 最终虚拟USDC:{}  ",walletEntity.getWallet(),balanceOf,JSON.toJSONString(ids),newVirtualUsdc);
			}
			walletsService.refresh(walletEntity);//刷新钱包
		}catch(Exception ex) {
			throw ex;
		}finally {
		    redisUtils.releaseGlobalLock(lockKey); // 4. 确保释放全局锁
	       log.info("{} --> 全局锁已释放",lockKey);
		}
	}
	
	/**
	 * 发送自动划转成功的Telegram通知
	 */
	private void sendAutoTransferNotification(WalletsEntity wallet, BigDecimal amount, String txHash) {
		try {
			String message = String.format(
				"🚀 自动划转成功！\n" +
				"💳 钱包地址: %s\n" +
				"💰 划转金额: %s USDC\n" +
				"📄 交易哈希: %s\n" +
				"✅ 状态: 成功",
				wallet.getWallet(),
				amount.toPlainString(),
				txHash
			);
			
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message);
			log.info("自动划转通知已加入队列 - 钱包: {}, 金额: {} USDC", wallet.getWallet(), amount);
			
		} catch (Exception e) {
			log.error("发送自动划转通知失败 - 钱包: {}, 错误: {}", wallet.getWallet(), e.getMessage());
		}
	}
	
	
	/**
	 * 发送自动划转失败的Telegram通知
	 */
	private void sendAutoTransferFailureNotification(WalletsEntity wallet, BigDecimal amount, String errorMessage) {
		try {
			String message = String.format(
				"❌ 自动划转失败！\n" +
				"💳 钱包地址: %s\n" +
				"💰 尝试划转金额: %s USDC\n" +
				"🚫 失败原因: %s\n" +
				"⚠️ 请检查钱包状态和余额",
				wallet.getWallet(),
				amount.toPlainString(),
				errorMessage
			);
			
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message);
			log.info("自动划转失败通知已加入队列 - 钱包: {}, 金额: {} USDC", wallet.getWallet(), amount);
			
		} catch (Exception e) {
			log.error("发送自动划转失败通知失败 - 钱包: {}, 错误: {}", wallet.getWallet(), e.getMessage());
		}
	}

}
