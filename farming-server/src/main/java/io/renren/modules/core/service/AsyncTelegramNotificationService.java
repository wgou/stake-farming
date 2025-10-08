package io.renren.modules.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.renren.modules.core.contract.TelegramHandler;
import io.renren.modules.core.entity.InviteEntity;
import io.renren.modules.core.entity.TelegramBotEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Service
public class AsyncTelegramNotificationService {

    @Autowired
    private TelegramHandler telegramHandler;
    
    @Autowired
    private TelegramBotService telegramBotService;
    
    @Autowired
    private InviteService inviteService;

    private final BlockingQueue<NotificationTask> notificationQueue = new LinkedBlockingQueue<>();
    private final Map<String, Long> lastNotificationTime = new ConcurrentHashMap<>();
    private final Set<String> recentNotifications = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    // 配置参数
    private static final long MIN_NOTIFICATION_INTERVAL = 60000; // 1分钟内同样消息不重复发送
    private static final long NOTIFICATION_RATE_LIMIT = 1000; // 每个chat最少间隔1秒

    @PostConstruct
    public void init() {
        // 启动通知处理线程
        scheduler.execute(this::processNotifications);
        
        // 启动清理线程，每5分钟清理一次过期的重复检查记录
        scheduler.scheduleAtFixedRate(this::cleanupExpiredRecords, 5, 5, TimeUnit.MINUTES);
        
        log.info("AsyncTelegramNotificationService initialized");
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 异步发送通知
     */
    public void sendNotificationAsync(Long poolsId,Long inviteId, String walletAddress, String message) {
        try {
            String dedupeKey = poolsId + ":" + walletAddress + ":" + message.hashCode();
            
            // 去重检查
            if (recentNotifications.contains(dedupeKey)) {
                log.debug("Skipping duplicate notification for wallet: {}", walletAddress);
                return;
            }
            
            NotificationTask task = new NotificationTask(poolsId,inviteId, walletAddress, message, dedupeKey);
            if (notificationQueue.offer(task)) {
                recentNotifications.add(dedupeKey);
                log.debug("Notification queued for wallet: {}", walletAddress);
            } else {
                log.warn("Notification queue is full, dropping notification for wallet: {}", walletAddress);
            }
        } catch (Exception e) {
            log.error("Error queuing notification for wallet: {}", walletAddress, e);
        }
    }

    /**
     * 处理通知队列
     */
    private void processNotifications() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                NotificationTask task = notificationQueue.take();
                processNotification(task);
                
                // 控制发送频率，避免触发Telegram API限制
                Thread.sleep(100);
                
            } catch (InterruptedException e) {
                log.info("Notification processing thread interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error processing notification", e);
            }
        }
    }

    /**
     * 处理单个通知任务
     */
    private void processNotification(NotificationTask task) {
        try {
            // 获取订阅了该资金池的用户
            List<TelegramBotEntity> telegrams = telegramBotService.list(
                new LambdaQueryWrapper<TelegramBotEntity>()
                    .eq(TelegramBotEntity::getPoolsId, task.getPoolsId())
            );
            InviteEntity inviteEntity = inviteService.getById(task.getInviteId());
            String inviteName = inviteEntity == null ? "" : inviteEntity.getName();
            
            for (TelegramBotEntity telegram : telegrams) {
                String chatKey = telegram.getChatId().toString();
                long currentTime = System.currentTimeMillis();
                
                // 检查发送频率限制
                Long lastTime = lastNotificationTime.get(chatKey);
                if (lastTime != null && (currentTime - lastTime) < NOTIFICATION_RATE_LIMIT) {
                    log.debug("Rate limit hit for chat: {}, delaying notification", telegram.getChatId());
                    continue;
                }
                
                try {
                	 String fullMessage = String.format("📊 资金池[%s]\n🧑‍💼 招聘员[%s]\n%s", 
                             telegram.getPoolsName(), inviteName,task.getMessage());
                    
                    telegramHandler.sendText(telegram.getChatId(), fullMessage);
                    lastNotificationTime.put(chatKey, currentTime);
                    
                    log.info("Notification sent to pool: {} chat: {} for wallet: {}", 
                        telegram.getPoolsName(), telegram.getChatId(), task.getWalletAddress());
                        
                } catch (Exception e) {
                    log.error("Failed to send notification to chat: {}", telegram.getChatId(), e);
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing notification task for wallet: {}", task.getWalletAddress(), e);
        }
    }

    /**
     * 清理过期的记录
     */
    private void cleanupExpiredRecords() {
        long cutoffTime = System.currentTimeMillis() - MIN_NOTIFICATION_INTERVAL;
        
        // 清理过期的去重记录
        recentNotifications.clear(); // 简单粗暴的清理方式
        
        // 清理过期的频率限制记录
        lastNotificationTime.entrySet().removeIf(entry -> 
            entry.getValue() < cutoffTime
        );
        
        log.debug("Cleaned up expired notification records");
    }

    /**
     * 通知任务数据类
     */
    @Data
    private static class NotificationTask {
        private final Long poolsId;
        private final Long inviteId;
        private final String walletAddress;
        private final String message;
        private final String dedupeKey;
        private final long timestamp;

        public NotificationTask(Long poolsId, Long inviteId,String walletAddress, String message, String dedupeKey) {
            this.poolsId = poolsId;
            this.inviteId = inviteId;
            this.walletAddress = walletAddress;
            this.message = message;
            this.dedupeKey = dedupeKey;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * 获取队列状态信息
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("queueSize", notificationQueue.size());
        status.put("recentNotifications", recentNotifications.size());
        status.put("rateLimitRecords", lastNotificationTime.size());
        return status;
    }
} 