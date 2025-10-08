package io.renren.modules.core.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.core.entity.TelegramBotEntity;
import io.renren.modules.core.mapper.TelegramBotMapper;
import io.renren.modules.core.param.PageParam;
import io.renren.modules.core.service.TelegramBotService;
@Service
public class TelegramBotServiceImpl  extends ServiceImpl<TelegramBotMapper, TelegramBotEntity> implements TelegramBotService {
	

	@Override
	public Page<TelegramBotEntity> listPage(PageParam param) {
		 Page<TelegramBotEntity> pageObject = new Page<TelegramBotEntity>(param.getCurrent(),param.getSize());
         pageObject.addOrder(OrderItem.desc("created"));
         Page<TelegramBotEntity> page = this.page(pageObject, new LambdaQueryWrapper<TelegramBotEntity>());
		return page;
	}
  
}
