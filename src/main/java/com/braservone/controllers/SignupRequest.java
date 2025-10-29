package com.braservone.controllers;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignupRequest {

  // Credenciais básicas
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(min = 6, max = 128)
  private String password;

  // Perfis/roles de login (ex.: ["user"], ["admin"], ["master"])
  private Set<String> role;

  // ---- Campos de PERFIL (opcionais) ----
  @Size(max = 100)
  private String nome;

  @Size(max = 15)
  private String cpf;

  // ID da empresa do usuário
  private Long empresaId;

  // Getters/Setters
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public Set<String> getRole() { return role; }
  public void setRole(Set<String> role) { this.role = role; }

  public String getNome() { return nome; }
  public void setNome(String nome) { this.nome = nome; }

  public String getCpf() { return cpf; }
  public void setCpf(String cpf) { this.cpf = cpf; }

  public Long getEmpresaId() { return empresaId; }
  public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
}
