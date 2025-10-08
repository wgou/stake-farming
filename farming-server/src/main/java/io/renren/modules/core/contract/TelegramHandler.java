package io.renren.modules.core.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.modules.core.entity.PoolsEntity;
import io.renren.modules.core.entity.TelegramBotEntity;
import io.renren.modules.core.service.PoolsService;
import io.renren.modules.core.service.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class TelegramHandler extends TelegramLongPollingBot  {
	
	@Autowired
	private TelegramBotService telegramBotService;
	@Autowired
	private PoolsService poolsService;
	
	@Value("${telegram.bot.username}")
	private String botUsername;
	
	@Value("${telegram.bot.token}")
	private String botToken;

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            log.info("Received message: '{}' from chatId: {}", text, chatId);
            if ("/start".equals(text)) {
                String username = update.getMessage().getFrom().getUserName();
                TelegramBotEntity telegram = telegramBotService.getOne(new LambdaQueryWrapper<TelegramBotEntity>().eq(TelegramBotEntity::getChatId, chatId));
                if(telegram ==null) {
                	telegram =new TelegramBotEntity();
                	telegram.setChatId(chatId);
                	telegram.setUserName(username);
                	telegramBotService.save(telegram);
                	log.info("Created new telegram user: {} with chatId: {}", username, chatId);
                }
                sendText(chatId, "你好 " + username + ",请输入你的后台登录账户：");
                return ;
            }
          
            TelegramBotEntity telegram = telegramBotService.getOne(new LambdaQueryWrapper<TelegramBotEntity>().eq(TelegramBotEntity::getChatId, chatId));
            if(telegram == null) {
            	 sendText(chatId, "请输入输入 /start 来重新开始订阅流程!");
            	 return ;
            }
            PoolsEntity pools = poolsService.getOne(new LambdaQueryWrapper<PoolsEntity>().eq(PoolsEntity::getOwnerName, text));
            if(pools == null) {
        	  sendText(chatId, "后台登录账户不存在,请重新输入!");
        	  return ;
            }
            telegramBotService.update(new LambdaUpdateWrapper<TelegramBotEntity>()
            		.set(TelegramBotEntity::getPoolsOwner, pools.getOwnerName())
            		.set(TelegramBotEntity::getPoolsId, pools.getId())
            		.set(TelegramBotEntity::getPoolsName, pools.getNickName())
            		.eq(TelegramBotEntity::getId, telegram.getId()));
            sendText(chatId, "✅ 你已成功订阅【"+pools.getNickName()+"】消息." );
        }
	}

 	public void sendText(Long chatId, String message) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(message);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

	@Override
	public String getBotUsername() {
		return botUsername;
	}
    @Override
    public String getBotToken() {
        return botToken;
    }
}
