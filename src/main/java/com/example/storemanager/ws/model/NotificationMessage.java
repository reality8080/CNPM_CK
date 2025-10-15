package com.example.storemanager.ws.model;

import lombok.Data;

@Data
public class NotificationMessage {
    private String title;
    private String content;
    private String type;
}
