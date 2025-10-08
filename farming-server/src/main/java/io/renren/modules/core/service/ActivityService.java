package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.ActivityEntity;
import io.renren.modules.core.param.ActivityPageParam;
import io.renren.modules.core.vo.ActivityPageVO;

public interface ActivityService extends IService<ActivityEntity> {

	Page<ActivityPageVO> listPage(ActivityPageParam param);
	
 
	
}
