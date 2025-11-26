package com.braservone.security.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.braservone.models.Account;
import com.braservone.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String email;

    @JsonIgnore
    private final String password;

    private final boolean enabled;

    private final Collection<? extends GrantedAuthority> authorities;

    // --- 1. CONSTRUTOR PRINCIPAL (Mapeia todos os campos) ---
    public UserDetailsImpl(
            String username,
            String email,
            String password,
            boolean enabled,
            Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    // --- 2. CONSTRUTOR SECUNDÁRIO (Para compatibilidade com Testes) ---
    // O teste passa: ID (String), Username, Email, Password, Authorities.
    // Como removemos o campo ID da classe, ignoramos o primeiro argumento e fixamos enabled = true.
    public UserDetailsImpl(String id, String username, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this(username, email, password, true, authorities);
    }

    // --- BUILDER (Usado na autenticação real) ---
    public static UserDetailsImpl build(Account account) {
        // Roles da account
        final List<GrantedAuthority> accAuthorities = account.getRoles().stream()
                .map(Role::getName)
                .map(er -> new SimpleGrantedAuthority(er.name()))
                .collect(Collectors.toList());

        // (Opcional) agregar roles do perfil
        final var merged = new HashSet<GrantedAuthority>(accAuthorities);
        if (account.getUser() != null && account.getUser().getRoles() != null) {
            account.getUser().getRoles().forEach(r -> 
                merged.add(new SimpleGrantedAuthority(r.getName().name()))
            );
        }

        final String email = account.getUser() != null ? account.getUser().getEmail() : null;

        return new UserDetailsImpl(
                account.getUsername(),
                email,
                account.getPassword(),
                account.isEnabled(),
                merged);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getEmail() {
        return email;
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl that = (UserDetailsImpl) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}