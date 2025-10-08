package io.renren.modules.core.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.constants.MessageStatusEnum;
import io.renren.modules.core.entity.MessageEntity;
import io.renren.modules.core.mapper.MessageMapper;
import io.renren.modules.core.service.MessageService;
@Service
public class MessageServiceImpl  extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService{

	@Override
	public Long saveMessage(String id,Long sender,Long reciver,String content,Integer type) {
		MessageEntity message = new MessageEntity();
		message.setMessageId(id);
		message.setSenderId(sender);
		message.setReciverId(reciver);
		message.setContent(content);
		message.setType(type);
		message.setStatus(MessageStatusEnum.UNREDY.getStatus());
		this.save(message);
		return message.getId();
		
	}
	
}
