package io.renren.modules.core.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.contract.ContractHandler;
import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.mapper.RewardMapper;
import io.renren.modules.core.param.RewardParam;
import io.renren.modules.core.service.RewardService;
import io.renren.modules.core.service.WalletsService;
@Service
public class RewardServiceImpl  extends ServiceImpl<RewardMapper, RewardEntity> implements RewardService {

	
	@Autowired
	ContractHandler handler;
	@Autowired
	RewardMapper rewardMapper;
	@Autowired
	WalletsService walletsService;
	
	@Override
	public Page<RewardEntity> listPage(RewardParam param) throws Exception {
		Page<RewardEntity> pageObject = new Page<RewardEntity>(param.getCurrent(),param.getSize());
        pageObject.addOrder(OrderItem.desc("created"));
        LambdaQueryWrapper<RewardEntity> query = new LambdaQueryWrapper<>();
		if(param.getInvited() !=null) {
			query.eq(RewardEntity::getInvited, param.getInvited());
		}
		if(CollectionUtils.isNotEmpty(param.getPoolsIds())) {
     		query.in(RewardEntity::getPoolsId, param.getPoolsIds());
		}
		if(StringUtils.isNotEmpty(param.getWallet())) {
			query.eq(RewardEntity::getWallet, param.getWallet());
		}
		Page<RewardEntity> page = this.page(pageObject, query);
		return page;
	}

	@Override
	public RewardEntity get(String wallet) throws Exception {
		RewardEntity reward = this.getOne(new LambdaQueryWrapper<RewardEntity>().eq(RewardEntity::getWallet, wallet));
		return reward;
	}

	@Override
	public BigDecimal sumReward() {
	    QueryWrapper<RewardEntity> queryWrapper = new QueryWrapper<>();
	    queryWrapper.select("SUM(reward_eth) as total");
	    Map<String, Object> result = rewardMapper.selectMaps(queryWrapper).get(0);
	    return (BigDecimal) result.get("total");
	}
	
	@Override
	public List<RewardEntity> getRandomRewardEntities(int limit) {
		 QueryWrapper<RewardEntity> query = new QueryWrapper<>();
		    query.last("WHERE id > (SELECT MAX(id)- 100 FROM s_reward) " +
		             "ORDER BY RAND() LIMIT " + limit);
		    return rewardMapper.selectList(query);
	}
	
}
