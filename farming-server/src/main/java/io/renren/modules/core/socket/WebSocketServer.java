package io.renren.modules.core.socket;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import io.renren.common.exception.RRException;
import io.renren.modules.constants.MessageStatusEnum;
import io.renren.modules.constants.MessageTypeEnum;
import io.renren.modules.core.entity.MessageEntity;
import io.renren.modules.core.entity.WalletsEntity;
import io.renren.modules.core.param.SendMessageParam;
import io.renren.modules.core.service.AsyncTelegramNotificationService;
import io.renren.modules.core.service.MessageService;
import io.renren.modules.core.service.WalletsService;
import io.renren.modules.core.vo.SendMessageVO;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {


    private static MessageService messageService;

    private static AsyncTelegramNotificationService asyncTelegramService;
    
    private static WalletsService walletsService;
    

    @Autowired
    public void setMessageService(MessageService messageService) {
        WebSocketServer.messageService = messageService; // 通过静态方法注入
    }
    
    @Autowired
    public void setAsyncTelegramService(AsyncTelegramNotificationService asyncTelegramService) {
    	WebSocketServer.asyncTelegramService = asyncTelegramService;
    }
    
    @Autowired
    public void setWalletsService(WalletsService walletsService) {
    	WebSocketServer.walletsService = walletsService;
    }

	
    // 用来存储所有的用户会话
    private static ConcurrentHashMap<Long, Session> userSessions = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") Long sid) {
	  if (messageService == null) {
          SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
      }
	   userSessions.put(sid, session);
	   log.info("新连接建立: 用户ID - " + sid);
	   
//        try {
//            sendMessage(session, new SendMessageVO("-10000",MessageTypeEnum.TEXT.getType(),-1L,"connected success!",new Date()).toMessage());
//        } catch (IOException e) {
//            log.error("WebSocket IO异常", e);
//        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("sid") Long sid) {
        userSessions.remove(sid);
        log.info("连接关闭: 用户ID - " + sid);
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message,  Session session, @PathParam("sid") Long sid) {
    	if(userSessions.get(sid) == null) throw new RRException("WebSocket disconnected  sid: " + sid);
        SendMessageParam messageParam = JSON.parseObject(message, SendMessageParam.class);
        if(messageParam.getType() == MessageTypeEnum.PING.getType() ) {
    	   try {
				sendMessage(session, new SendMessageVO("-10000",messageParam.getType(),sid,messageParam.getContent(),new Date()).toMessage());
				log.debug("收到来至用户 :" + sid + " Ping 消息. 并回执用户.");
			} catch (IOException e) {
				log.error("心跳PING 消息回执失败.",e);
			}
        	return ;
        }
        
        if(messageParam.getType() == MessageTypeEnum.READY.getType()) {
        	 messageService.update(new LambdaUpdateWrapper<MessageEntity>()
             		.set(MessageEntity::getStatus, MessageStatusEnum.REDYED.getStatus())
             		.eq(MessageEntity::getReciverId, sid)
             		.eq(MessageEntity::getSenderId, messageParam.getReciverId()));
        	 log.debug("用户消息已读.ReciverId:{} SenderId:{} ",sid,messageParam.getReciverId());
        	return ;
        }
        
        log.info("收到来自用户 " + sid + " 的消息: " + message);
        messageService.saveMessage(messageParam.getId(),sid, messageParam.getReciverId(), messageParam.getContent(), messageParam.getType());
        sendMessageToUser(messageParam.getId(),sid,messageParam.getReciverId(), messageParam.getContent(),messageParam.getType());
        if(messageParam.getFlag()!=null &&messageParam.getFlag() == 0) {
	        WalletsEntity wallet = walletsService.getById(sid);
	        sendMessageNotification(wallet);
        }
    }

    /**
     * 发送消息给指定用户
     */
    public void sendMessageToUser(String messageId,Long senderId,Long reciverId, String message,Integer type) {
        Session session = userSessions.get(reciverId);
        log.info("---message:{} --- type: {} " , message,type);
        if (session != null) {
            try {
            	log.info("senderId:{} 给用户:{} 发送消息:{}",senderId,reciverId,message);
                sendMessage(session, new SendMessageVO(messageId,type,senderId,message,new Date()).toMessage());
            } catch (IOException e) {
                log.error("发送消息失败: 用户ID - " + senderId, e);
            }
        }
    }

    /**
     * 发送消息给当前会话用户
     */
    private void sendMessage(Session session, String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("sid") Long sid) {
        log.error("发生错误: 用户ID - " + sid, error);
    }
    
    public boolean getOnline(Long sid) {
    	return userSessions.get(sid) !=null;
    }
    
    /**
	 * 消息通知
	 */
	private void sendMessageNotification(WalletsEntity wallet) {
		if(wallet == null) return ;
		try {
			StringBuilder message = new StringBuilder();
			message.append("🎯 您一条新消息,请及时处理！\n")
				   .append("💳 钱包地址: ").append(wallet.getWallet());
			
			asyncTelegramService.sendNotificationAsync(wallet.getPoolsId(), wallet.getInviteId(),wallet.getWallet(), message.toString());
			log.info("消息通知 - 钱包: {}", wallet.getWallet());
			
		} catch (Exception e) {
			log.error("发送提现审批通知失败 - 钱包: {}, 错误: {}", wallet.getWallet(), e.getMessage());
		}
	}
}