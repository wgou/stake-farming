package io.renren.modules.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_invite")
public class InviteEntity extends BaseEntity {

	private String name;
	
	private String code;
	
	private String inviteUrl;
	
	
}
