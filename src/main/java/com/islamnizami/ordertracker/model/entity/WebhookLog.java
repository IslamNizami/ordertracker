package com.islamnizami.ordertracker.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_logs")
@Data
@NoArgsConstructor
public class WebhookLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;  // payment , shipment etc

    @Column(columnDefinition = "TEXT",nullable = false)
    private String payload; //for raw JSON

    @Column(nullable = false)
    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime receivedAt;

    public WebhookLog(String eventType, String payload, String status) {
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
    }
}
