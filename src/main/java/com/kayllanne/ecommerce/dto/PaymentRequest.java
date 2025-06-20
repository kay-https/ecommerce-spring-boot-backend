package com.kayllanne.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "O ID do pedido não pode ser nulo.")
    private Long orderId;

    @NotNull(message = "O valor do pagamento não pode ser nulo.")
    @DecimalMin(value = "0.01", message = "O valor do pagamento deve ser maior que zero.")
    private BigDecimal amount;

    @NotBlank(message = "O método de pagamento não pode estar em branco.")
    private String paymentMethod;

    @Size(max = 255, message = "Os detalhes do cartão não podem exceder 255 caracteres.")
    private String cardDetails;
}