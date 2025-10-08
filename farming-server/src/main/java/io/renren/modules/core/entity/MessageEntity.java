package io.renren.modules.core.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@TableName("s_message")
public class MessageEntity {

	@Id
	private Long id;
	
	private String messageId;
	
    private Long senderId;
    
    private Long reciverId;
    
    private String content;
    
    private Integer type; //0 文本消息  1 图片
    
    private Integer status; 
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
}
