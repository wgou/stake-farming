package io.renren.modules.core.vo;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class SendMessageVO {

	private String id;
	
	private Integer type;
	
	private Long senderId;
	
	private String content;
	
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	
	
	
	
	public String toMessage() {
		return JSON.toJSONString(this);
	}
}
