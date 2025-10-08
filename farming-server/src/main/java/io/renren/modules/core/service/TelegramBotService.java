package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.TelegramBotEntity;
import io.renren.modules.core.param.PageParam;

public interface TelegramBotService extends IService<TelegramBotEntity> {

	Page<TelegramBotEntity> listPage(PageParam param);
	
 
	
}
