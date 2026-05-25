package com.bacpham.saas.listeners;

import com.bacpham.saas.events.TenantRegisteredEvent;
import com.bacpham.saas.services.impl.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantEventListener {

    private final TelegramNotificationService telegramService;

    @Async
    @EventListener
    public void handleTenantRegistration(TenantRegisteredEvent event) {
        log.info("Received tenant registration event for company: {} [Thread: {}]", 
            event.getCompanyName(), Thread.currentThread().getName());
        
        // Gửi Telegram cho ban quản trị
        telegramService.sendApprovalNotification(
            event.getCompanyName(),
            event.getAdminEmail(),
            event.getCompanyCode()
        );
    }
}
