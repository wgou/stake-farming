package io.renren.modules.core.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.renren.modules.core.entity.MessageEntity;
import lombok.Data;
@Data
public class MessageVO {


	private String id;
	
    private Long senderId;
    
    private Long reciverId;
    
    private String content;
    
    private Integer type; //0 文本消息  1 图片
    
    private Integer status; 
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
    
    
    public MessageVO build(MessageEntity message ) {
    	this.id = message.getMessageId();
    	this.senderId = message.getSenderId();
    	this.reciverId = message.getReciverId();
    	this.content = message.getContent();
    	this.type = message.getType();
    	this.status = message.getStatus();
    	this.created = message.getCreated();
    	this.modified = message.getModified();
    	return this;
    }

}
