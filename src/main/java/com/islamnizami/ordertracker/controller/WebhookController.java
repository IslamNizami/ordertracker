package com.islamnizami.ordertracker.controller;


import com.islamnizami.ordertracker.model.entity.WebhookLog;
import com.islamnizami.ordertracker.repository.WebhookLogRepository;
import com.islamnizami.ordertracker.service.WebhookProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private static final Logger logger = Logger.getLogger(WebhookController.class.getName());

    private final WebhookLogRepository webhookLogRepository;
    private final WebhookProcessorService webhookProcessorService;

    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody String payload){
        logger.info("Received Payment Webhook");

        WebhookLog logEntry = new WebhookLog("payment",payload,"RECEIVED");
        logEntry = webhookLogRepository.save(logEntry);

        webhookProcessorService.processPaymentWebhook(logEntry.getId());

        return ResponseEntity.ok("Webhook received.");
    }

    @PostMapping("/shipment")
    public ResponseEntity<String> handleShipmentWebhook(@RequestBody String payload){
        logger.info("Received Shipment Webhook");

        WebhookLog logEntry = new WebhookLog("shipment",payload,"RECEIVED");
        webhookLogRepository.save(logEntry);

        webhookProcessorService.processShipmentWebhook(logEntry.getId());

        return ResponseEntity.ok("Webhook received.");
    }
}
