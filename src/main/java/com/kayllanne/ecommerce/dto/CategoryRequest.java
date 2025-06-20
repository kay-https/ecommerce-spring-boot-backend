package com.kayllanne.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "O nome da categoria não pode estar em branco.")
    @Size(min = 3, max = 100, message = "O nome da categoria deve ter entre 3 e 100 caracteres.")
    private String name;
}