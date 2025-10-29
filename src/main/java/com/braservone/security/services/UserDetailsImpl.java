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
import com.braservone.models.Empresa;
import com.braservone.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private final String username;
  private final String email;

  @JsonIgnore
  private final String password;

  private final boolean enabled;

  private final Empresa empresa; // 👈 agora incluímos a empresa

  private final Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(
      String username,
      String email,
      String password,
      boolean enabled,
      Empresa empresa,
      Collection<? extends GrantedAuthority> authorities
  ) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.enabled = enabled;
    this.empresa = empresa;
    this.authorities = authorities;
  }

  /**
   * Constrói o UserDetails a partir da Account (credenciais).
   * Agrega roles de account e, opcionalmente, roles do perfil (user).
   */
  public static UserDetailsImpl build(Account account) {
    // Roles da account
    final List<GrantedAuthority> accAuthorities = account.getRoles().stream()
        .map(Role::getName) // ERole
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
    final Empresa empresa = account.getUser() != null ? account.getUser().getEmpresa() : null;

    return new UserDetailsImpl(
        account.getUsername(),
        email,
        account.getPassword(),
        account.isEnabled(),
        empresa,
        merged
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public String getEmail() {
    return email;
  }

  public Empresa getEmpresa() {
    return empresa;
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
