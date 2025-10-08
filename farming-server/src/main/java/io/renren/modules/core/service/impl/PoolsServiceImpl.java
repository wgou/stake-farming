package io.renren.modules.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.mapper.PoolsMapper;
import io.renren.modules.core.service.InviteService;
import io.renren.modules.core.service.PoolsService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class PoolsServiceImpl extends ServiceImpl<PoolsMapper, PoolsEntity> implements PoolsService {
	
	@Autowired
	InviteService inviteService;
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(Long id) {
		//删除钱包数据
		//删除划账数据
		//删除奖励数据
		//删除归集数据
		//删除业务员数据
		inviteService.remove(new LambdaQueryWrapper<InviteEntity>().eq(InviteEntity::getPoolsId, id));
		//删除资金池数据
		this.remove(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getId, id));
		
	}
 

}
