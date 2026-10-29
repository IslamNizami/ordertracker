package com.islamnizami.ordertracker.model.dto;

import com.islamnizami.ordertracker.model.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponse {

    private Long id;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus().name();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}
