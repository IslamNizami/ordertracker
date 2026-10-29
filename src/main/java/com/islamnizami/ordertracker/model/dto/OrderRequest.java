package com.islamnizami.ordertracker.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {

    @NotNull
    @DecimalMin(value = "0.01",message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;
}
