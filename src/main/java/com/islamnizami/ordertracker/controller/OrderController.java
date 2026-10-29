package com.islamnizami.ordertracker.controller;

import com.islamnizami.ordertracker.model.dto.OrderRequest;
import com.islamnizami.ordertracker.model.dto.OrderResponse;
import com.islamnizami.ordertracker.service.CsvExportService;
import com.islamnizami.ordertracker.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CsvExportService csvExportService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest, Principal principal){
        OrderResponse response = orderService.createOrder(orderRequest, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(Principal principal){
        List<OrderResponse> responses = orderService.getUserOrders(principal.getName());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id, Principal principal){
        try{
            OrderResponse response = orderService.getOrderById(id,principal.getName());
            return ResponseEntity.ok(response);
        }catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportOrdersToCsv(Principal principal){
        List<OrderResponse> orders = orderService.getUserOrders(principal.getName());
        InputStreamResource file = new InputStreamResource(csvExportService.exportOrdersToCsv(orders));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + "orders.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
