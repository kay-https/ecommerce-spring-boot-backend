package com.kayllanne.ecommerce.dto;

import com.kayllanne.ecommerce.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String username;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;

}