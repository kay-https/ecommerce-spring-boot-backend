package com.kayllanne.ecommerce.repository;

import com.kayllanne.ecommerce.entity.CartItem;
import com.kayllanne.ecommerce.entity.CartItemId; // Importe a classe de ID composta
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
}