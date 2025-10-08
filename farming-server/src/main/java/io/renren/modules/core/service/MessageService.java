package io.renren.modules.core.service;

import com.baomidou.mybatisplus.extension.service.IService;

import io.renren.modules.core.entity.MessageEntity;

public interface MessageService extends IService<MessageEntity> {

	
	Long saveMessage(String id,Long sender,Long reciver,String content,Integer type);
	
}
