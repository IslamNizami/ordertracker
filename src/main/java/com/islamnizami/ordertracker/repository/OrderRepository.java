package com.islamnizami.ordertracker.repository;

import com.islamnizami.ordertracker.model.entity.Order;
import com.islamnizami.ordertracker.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByIdAndUser(Long id, User user);
    Optional<Order> findByExternalTransactionId(String externalTransactionId);
}
