package io.renren.modules.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("s_telegram_bot")
public class TelegramBotEntity extends BaseEntity {

	private Long chatId;
	
	private String userName;
	
	private String poolsOwner;
	
	private String poolsName;
}
