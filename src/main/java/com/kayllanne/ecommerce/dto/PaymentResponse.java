package com.kayllanne.ecommerce.dto;

import com.kayllanne.ecommerce.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long orderId;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String transactionId;
    private String paymentStatus;
    private OrderStatus newOrderStatus;
    private LocalDateTime paymentDate;
    private String message;
}