package io.renren.modules.core.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.R;
import io.renren.modules.core.service.AsyncTelegramNotificationService;

/**
 * Telegram通知服务监控接口
 */
@RestController
@RequestMapping("/telegram/monitor")
public class TelegramMonitorController {

    @Autowired
    private AsyncTelegramNotificationService asyncTelegramService;

    /**
     * 获取Telegram通知服务状态
     */
    @GetMapping("/status")
    public R getStatus() {
        Map<String, Object> status = asyncTelegramService.getStatus();
        return R.ok().put("data", status);
    }
} 