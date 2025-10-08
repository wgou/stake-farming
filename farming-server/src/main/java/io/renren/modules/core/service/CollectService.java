package io.renren.modules.core.service;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.CollectEntity;
import io.renren.modules.core.param.CollectPageParam;

public interface CollectService extends IService<CollectEntity> {

	Page<CollectEntity> listPage(CollectPageParam param);
	
	BigDecimal sumUsdc(CollectPageParam param);
	
	BigDecimal sumEth(CollectPageParam param);
	
}
