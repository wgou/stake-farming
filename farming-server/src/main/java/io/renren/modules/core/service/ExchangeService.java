package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.ExchangeEntity;

public interface ExchangeService  extends IService<ExchangeEntity> {

	void exchange(String wallet)  throws Exception;
}
