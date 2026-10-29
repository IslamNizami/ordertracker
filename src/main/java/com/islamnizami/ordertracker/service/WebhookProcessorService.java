package com.islamnizami.ordertracker.service;

import com.islamnizami.ordertracker.model.entity.Order;
import com.islamnizami.ordertracker.model.entity.WebhookLog;
import com.islamnizami.ordertracker.model.enums.OrderStatus;
import com.islamnizami.ordertracker.repository.OrderRepository;
import com.islamnizami.ordertracker.repository.WebhookLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class WebhookProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookProcessorService.class);


    private final WebhookLogRepository webhookLogRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;


    @Async
    @Transactional
    public void processPaymentWebhook(Long webhookLogId){
        logger.info("Asynchronously processing webhook log ID: {}", webhookLogId);

        WebhookLog logEntry = webhookLogRepository.findById(webhookLogId).orElse(null);
        if(logEntry == null){
            return;
        }
        try{
            JsonNode rootNode = objectMapper.readTree(logEntry.getPayload());

            String eventType = rootNode.path("type").asText();
            String transactionId = rootNode.path("data")
                    .path("object")
                    .path("metadata")
                    .path("externalTransactionId").asText();

            if(transactionId == null || transactionId.isEmpty()){
                logEntry.setStatus("FAILED: NO_TXN_ID");
                webhookLogRepository.save(logEntry);
                return;
            }

            Order order = orderRepository.findByExternalTransactionId(transactionId).orElse(null);
            if(order == null){
                logEntry.setStatus("FAILED: ORDER_NOT_FOUND");
                webhookLogRepository.save(logEntry);
                return;
            }
            if("payment_intent.succeeded".equals(eventType)){
                order.setStatus(OrderStatus.PAID);
            }else if ("payment_intent.payment_failed".equals(eventType)){
                order.setStatus(OrderStatus.FAILED);
            }

            orderRepository.save(order);
            logEntry.setStatus("PROCESSED");
            webhookLogRepository.save(logEntry);

            emailService.sendOrderStatusEmail(order.getUser().getEmail(),order.getId(),order.getStatus().name());

        }catch (Exception e){
            logger.error("Error processing webhook payload: {}",e.getMessage());
            logEntry.setStatus("FAILED: EXCEPTION");
            webhookLogRepository.save(logEntry);
        }
    }

    @Async
    @Transactional
    public void processShipmentWebhook(Long webhookLogId){
        logger.info("Processing Shipment Webhook Log ID: {}", webhookLogId);
        WebhookLog logEntry = webhookLogRepository.findById(webhookLogId).orElse(null);
        if(logEntry == null){
            return;
        }

        try{
            JsonNode rootNode = objectMapper.readTree(logEntry.getPayload());
            String eventType = rootNode.path("event").asText();
            String transactionId = rootNode.path("externalTransactionId").asText();

            Order order = orderRepository.findByExternalTransactionId(transactionId).orElse(null);
            if(order == null){
                logEntry.setStatus("FAILED: ORDER_NOT_FOUND");
                webhookLogRepository.save(logEntry);
                return;
            }

            if("shipment.shipped".equals(eventType)){
                order.setStatus(OrderStatus.SHIPPED);
            }else if("shipment.delivered".equals(eventType)){
                order.setStatus(OrderStatus.DELIVERED);
            }

            orderRepository.save(order);
            logEntry.setStatus("PROCESSED");
            webhookLogRepository.save(logEntry);

            emailService.sendOrderStatusEmail(order.getUser().getEmail(),order.getId(),order.getStatus().name());
        }catch (Exception e){
            logger.error("Error processing shipment webhook: {}", e.getMessage());
            logEntry.setStatus("FAILED: EXCEPTION");
            webhookLogRepository.save(logEntry);
        }

    }

}
