package com.islamnizami.ordertracker.service;

import com.islamnizami.ordertracker.model.dto.OrderRequest;
import com.islamnizami.ordertracker.model.dto.OrderResponse;
import com.islamnizami.ordertracker.model.entity.Order;
import com.islamnizami.ordertracker.model.entity.User;
import com.islamnizami.ordertracker.repository.OrderRepository;
import com.islamnizami.ordertracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderResponse createOrder(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = new Order(user,request.getTotalAmount());


        order.setExternalTransactionId("txn_"+ UUID.randomUUID().toString());

        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = orderRepository.findByIdAndUser(orderId,user)
                .orElseThrow(()-> new RuntimeException("Order not found or unauthroized "));

        return new OrderResponse(order);
    }

}
