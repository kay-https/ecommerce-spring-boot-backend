package com.kayllanne.ecommerce.service;

import com.kayllanne.ecommerce.dto.UserProfileRequest;
import com.kayllanne.ecommerce.dto.UserProfileResponse;
import com.kayllanne.ecommerce.entity.User;
import com.kayllanne.ecommerce.exception.ResourceNotFoundException;
import com.kayllanne.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        return mapToUserProfileResponse(user);
    }
    @Transactional
    public UserProfileResponse updateUserProfile(String username, UserProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        if (request.getEmail() != null && !request.getEmail().isEmpty() && !user.getEmail().equals(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (request.getUsername() != null && !request.getUsername().isEmpty() && !user.getUsername().equals(request.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent() && !request.getUsername().equals(user.getUsername())) { // Adiciona check para não comparar com o próprio nome
                throw new RuntimeException("Nome de usuário '" + request.getUsername() + "' já está em uso.");
            }
            user.setUsername(request.getUsername());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserProfileResponse(updatedUser);
    }
    private UserProfileResponse mapToUserProfileResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().replace("ROLE_", ""))
                .collect(Collectors.toSet());

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roleNames) // Passando o Set de roles
                .build();
    }
}