package io.renren.modules.core.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.constants.CollectCoinEnum;
import io.renren.modules.constants.TransferEnum;
import io.renren.modules.core.entity.CollectEntity;
import io.renren.modules.core.mapper.CollectMapper;
import io.renren.modules.core.param.CollectPageParam;
import io.renren.modules.core.service.CollectService;
@Service
public class CollectServiceImpl  extends ServiceImpl<CollectMapper, CollectEntity> implements CollectService {

	@Override
	public Page<CollectEntity> listPage(CollectPageParam param) {
		 Page<CollectEntity> pageObject = new Page<CollectEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         LambdaQueryWrapper<CollectEntity> inviteQuery = buildQuery(param);
         Page<CollectEntity> page = this.page(pageObject, inviteQuery);
		return page;
	}

	@Override
	public BigDecimal sumUsdc(CollectPageParam param) {
		List<CollectEntity> lists = this.list(buildQuery(param));
		BigDecimal totalUsdc =  lists==null?BigDecimal.ZERO : lists.stream()
				.filter(w -> w.getStatus() == TransferEnum.yes.getCode())
				.filter(w -> w.getCoin().equals(CollectCoinEnum.USDC.name()))
				.map(CollectEntity::getAmount)
				.reduce(BigDecimal.ZERO,BigDecimal::add);
		return totalUsdc;
	
	}
	@Override
	public BigDecimal sumEth(CollectPageParam param) {
		List<CollectEntity> lists = this.list(buildQuery(param));
		BigDecimal totalEth =  lists==null?BigDecimal.ZERO : lists.stream()
				.filter(w -> w.getStatus() == TransferEnum.yes.getCode())
				.filter(w -> w.getCoin().equals(CollectCoinEnum.ETH.name()))
				.map(CollectEntity::getAmount)
				.reduce(BigDecimal.ZERO,BigDecimal::add);
		return totalEth;
	}
	
	private LambdaQueryWrapper<CollectEntity> buildQuery(CollectPageParam param){
	   LambdaQueryWrapper<CollectEntity> inviteQuery = new LambdaQueryWrapper<>();
         if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
        	 inviteQuery.in(CollectEntity::getPoolsId, param.getPoolsIds() );
         }
         if(param.getStatus() !=null) {
        	 inviteQuery.eq(CollectEntity::getStatus, param.getStatus() );
         }
         if(StringUtils.isNotBlank(param.getFromWallet())) {
        	 inviteQuery.eq(CollectEntity::getFromWallet, param.getFromWallet() );
         }
         if(StringUtils.isNotBlank(param.getToWallet())) {
        	 inviteQuery.eq(CollectEntity::getToWallet, param.getToWallet() );
         }
         if(StringUtils.isNotBlank(param.getCoin())) {
        	 inviteQuery.eq(CollectEntity::getCoin, param.getCoin() );
         }
         if(param.getStart() !=null) {
        	 inviteQuery.ge(CollectEntity::getCreated, param.getStart() );
         }
         if(param.getEnd() !=null) {
        	 inviteQuery.le(CollectEntity::getCreated, param.getEnd() );
         }
         return inviteQuery;
	}

}
