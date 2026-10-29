package com.islamnizami.ordertracker.repository;

import com.islamnizami.ordertracker.model.entity.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebhookLogRepository extends JpaRepository<WebhookLog,Long> {
    List<WebhookLog> findByStatus(String status);
    List<WebhookLog> findByEventType(String eventType);
    List<WebhookLog> findByEventTypeAndStatus(String eventType, String status);
    List<WebhookLog> findAllByOrderByReceivedAtDesc();
}
