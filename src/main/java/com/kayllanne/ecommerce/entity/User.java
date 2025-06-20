package com.kayllanne.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Importe esta interface

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Mantém o builder do Lombok
public class User implements UserDetails { // Adicione 'implements UserDetails'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // --- Métodos da interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Por enquanto, sempre true. Em produção, você pode ter lógica de expiração de conta.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Por enquanto, sempre true. Em produção, você pode ter lógica de bloqueio de conta.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por enquanto, sempre true. Em produção, você pode ter lógica de expiração de credenciais.
    }

    @Override
    public boolean isEnabled() {
        return true; // Por enquanto, sempre true. Em produção, você pode ter lógica de ativação/desativação de usuário.
    }
}