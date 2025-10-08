package io.renren.modules.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.RewardEntity;
import io.renren.modules.core.param.RewardParam;


public interface RewardService  extends IService<RewardEntity> {
	
	Page<RewardEntity> listPage(RewardParam param) throws Exception;
	
	RewardEntity get(String wallet) throws Exception;
	
	BigDecimal sumReward()throws Exception ;
	
	List<RewardEntity> getRandomRewardEntities(int limit);
}
