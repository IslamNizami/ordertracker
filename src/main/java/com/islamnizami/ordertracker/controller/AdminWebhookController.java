package com.islamnizami.ordertracker.controller;
import com.islamnizami.ordertracker.model.dto.WebhookLogResponse;
import com.islamnizami.ordertracker.service.WebhookLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/webhooks")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminWebhookController {


    private final WebhookLogService webhookLogService;

    @GetMapping
    public ResponseEntity<List<WebhookLogResponse>> getWebhookLogs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eventType) {
        List<WebhookLogResponse> logs = webhookLogService.getAllLogs(status, eventType);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebhookLogResponse> getWebhookLogById(@PathVariable Long id) {
        try {
            WebhookLogResponse log = webhookLogService.getLogById(id);
            return ResponseEntity.ok(log);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}