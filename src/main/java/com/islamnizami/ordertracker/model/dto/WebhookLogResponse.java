package com.islamnizami.ordertracker.model.dto;

import com.islamnizami.ordertracker.model.entity.WebhookLog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WebhookLogResponse {
    private Long id;
    private String eventType;
    private String payload;
    private String status;
    private LocalDateTime receivedAt;

    public WebhookLogResponse(WebhookLog log) {
        this.id = log.getId();
        this.eventType = log.getEventType();
        this.payload = log.getPayload();
        this.status = log.getStatus();
        this.receivedAt = log.getReceivedAt();
    }
}
