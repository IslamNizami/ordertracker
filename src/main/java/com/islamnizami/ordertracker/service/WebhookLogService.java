package com.islamnizami.ordertracker.service;


import com.islamnizami.ordertracker.model.dto.WebhookLogResponse;
import com.islamnizami.ordertracker.model.entity.WebhookLog;
import com.islamnizami.ordertracker.repository.WebhookLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebhookLogService {


    private final WebhookLogRepository webhookLogRepository;

    public List<WebhookLogResponse> getAllLogs(String status, String eventType) {
        List<WebhookLog> logs;

        if (status != null && eventType != null) {
            logs = webhookLogRepository.findByEventTypeAndStatus(eventType, status);
        } else if (status != null) {
            logs = webhookLogRepository.findByStatus(status);
        } else if (eventType != null) {
            logs = webhookLogRepository.findByEventType(eventType);
        } else {
            logs = webhookLogRepository.findAllByOrderByReceivedAtDesc();
        }

        return logs.stream().map(WebhookLogResponse::new).collect(Collectors.toList());
    }

    public WebhookLogResponse getLogById(Long id) {
        WebhookLog log = webhookLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webhook Log not found with ID: " + id));
        return new WebhookLogResponse(log);
    }
}