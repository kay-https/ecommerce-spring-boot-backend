package com.kayllanne.ecommerce.repository;

import com.kayllanne.ecommerce.entity.Order;
import com.kayllanne.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}