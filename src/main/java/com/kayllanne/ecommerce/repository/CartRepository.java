package com.kayllanne.ecommerce.repository;

import com.kayllanne.ecommerce.entity.Cart;
import com.kayllanne.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = "cartItems")
    Optional<Cart> findByUser(User user);
}