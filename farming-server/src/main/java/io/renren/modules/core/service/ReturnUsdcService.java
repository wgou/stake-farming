package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.ReturnUsdcEntity;
import io.renren.modules.core.param.ReturnParam;

public interface ReturnUsdcService extends IService<ReturnUsdcEntity> {

	
	Page<ReturnUsdcEntity> listPage(ReturnParam param);
	
	
}
