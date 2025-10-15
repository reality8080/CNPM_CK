package com.example.storemanager.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SimpMessagingTemplate template;
    public NotificationService(SimpMessagingTemplate template){ this.template = template; }

    public void sendPromotionNotification(Object payload){
        template.convertAndSend("/topic/promotions", payload);
    }

    public void sendReportNotification(Object payload){
        template.convertAndSend("/topic/reports", payload);
    }
}
