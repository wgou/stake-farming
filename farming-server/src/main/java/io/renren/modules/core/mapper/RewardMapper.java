package io.renren.modules.core.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.core.entity.RewardEntity;

@Mapper
public interface RewardMapper  extends BaseMapper<RewardEntity> {
	
	// 在RewardMapper中添加
		@Select("SELECT MAX(id) FROM reward_entity")
		Long selectMaxId();

}
