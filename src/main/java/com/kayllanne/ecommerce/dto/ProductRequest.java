package com.kayllanne.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class ProductRequest {

    @NotBlank(message = "O nome do produto não pode estar em branco.")
    @Size(min = 3, max = 255, message = "O nome do produto deve ter entre 3 e 255 caracteres.")
    private String name;

    @Size(max = 1000, message = "A descrição não pode exceder 1000 caracteres.")
    private String description;

    @NotNull(message = "O preço do produto não pode ser nulo.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal price;

    @NotNull(message = "A quantidade em estoque não pode ser nula.")
    @Min(value = 0, message = "A quantidade não pode ser negativa.")
    private Integer quantity;

    private String imageUrl;

    @NotNull(message = "O ID da categoria não pode ser nulo.")
    private Long categoryId;
}