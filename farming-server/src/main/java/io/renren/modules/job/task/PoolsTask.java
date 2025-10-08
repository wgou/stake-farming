package io.renren.modules.job.task;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.service.PoolsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.commons.collection.Lists;
/**
 * Pools Task 用于更新余额
 * @author Administrator
 *
 */
@Slf4j
@Component
public class PoolsTask extends BaseTask {
	@Autowired
	PoolsService poolsService;
	@Autowired
	ContractHandler handler;

	
	@Override
	public void run(String params) throws Exception {
		proccess(params);
	}
	
	private List<PoolsBalanceInfo> batchGetBalances(List<PoolsEntity> wallets) {
        return wallets.parallelStream()
            .map(wallet -> {
                try {
                    return new PoolsBalanceInfo(
                		wallet.getId(),
                        handler.getEthBalance(wallet.getWallet()),
                        handler.getUsdcBalance(wallet.getWallet()),
                        handler.getEthBalance(wallet.getApproveWallet())
                    );
                } catch (Exception e) {
                    log.error("PoolsTask -> 获取余额异常: {}", wallet.getWallet(), e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
	
	@Override
	public void handler(String params) throws Exception {
		List<PoolsEntity> wallets = poolsService.list();
		log.info("PoolsTask -> 开始更新资金池余额数据.. {} 条.",wallets.size());
		List<PoolsBalanceInfo> datas = batchGetBalances(wallets);
		List<PoolsEntity>updateList = Lists.newArrayList();
		datas.forEach(balance->{
            PoolsEntity updateEntity = new PoolsEntity();
            updateEntity.setId(balance.getId());
            updateEntity.setEth(balance.getUserEth());
            updateEntity.setUsdc(balance.getUserUsdc());
            updateEntity.setApproveEth(balance.getApproveEth());
            updateList.add(updateEntity);
		});
		  // 执行批量更新
	    if (!updateList.isEmpty()) {
	        poolsService.updateBatchById(updateList);
	    }
		log.info("PoolsTask -> 资金池余额数据更新完成.. {} 条.",wallets.size());
	}
	
	@Data
	@AllArgsConstructor
	private static class PoolsBalanceInfo {
		private Long id;
	    private BigDecimal userEth;
	    private BigDecimal userUsdc;
	    private BigDecimal approveEth;
	}
}
