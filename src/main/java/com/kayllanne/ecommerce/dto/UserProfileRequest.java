package com.kayllanne.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {

    @Size(max = 255, message = "O nome de usuário não pode exceder 255 caracteres.")
    private String username;

    @Email(message = "Por favor, insira um endereço de email válido.")
    @Size(max = 255, message = "O email não pode exceder 255 caracteres.")
    private String email;
}