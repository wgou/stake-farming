package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.PoolsEntity;

public interface PoolsService extends IService<PoolsEntity> {

	
	void delete(Long id);
}
