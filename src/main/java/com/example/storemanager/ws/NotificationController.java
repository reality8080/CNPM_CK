package com.example.storemanager.ws;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/notify")  // client gửi tới /app/notify
    @SendTo("/topic/notifications") // server gửi lại cho client
    public String handleNotification(String message) {
        return "Server received: " + message;
    }
}
