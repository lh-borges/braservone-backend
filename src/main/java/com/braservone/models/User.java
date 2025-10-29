package com.braservone.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "cpf")
})
public class User {
  @Id
  @NotBlank
  @Size(max = 20)
  @Column(name = "username", length = 20, nullable = false)
  private String username;

  private String srcImage;

  @NotBlank @Size(max = 100)
  @Column(length = 100, nullable = false)
  private String nome;

  @NotBlank @Email @Size(max = 50)
  @Column(length = 50, nullable = false)
  private String email;

  @Size(max = 15)
  @Column(length = 15, nullable = false)
  private String cpf;

  @ManyToOne(optional = false)
  @JoinColumn(name = "empresa_id", nullable = false)
  private Empresa empresa;

  // @JsonIgnore
  // @Column(length = 120) private String password;

  @ManyToMany(fetch = FetchType.LAZY)
  private Set<Role> roles = new HashSet<>();

  public String getUsername() {
	return username;
  }

  public void setUsername(String username) {
	this.username = username;
  }

  public String getSrcImage() {
	return srcImage;
  }

  public void setSrcImage(String srcImage) {
	this.srcImage = srcImage;
  }

  public String getNome() {
	return nome;
  }

  public void setNome(String nome) {
	this.nome = nome;
  }

  public String getEmail() {
	return email;
  }

  public void setEmail(String email) {
	this.email = email;
  }

  public String getCpf() {
	return cpf;
  }

  public void setCpf(String cpf) {
	this.cpf = cpf;
  }

  public Empresa getEmpresa() {
	return empresa;
  }

  public void setEmpresa(Empresa empresa) {
	this.empresa = empresa;
  }

  public Set<Role> getRoles() {
	return roles;
  }

  public void setRoles(Set<Role> roles) {
	this.roles = roles;
  }

  public void updateUser(String nome, String email) {
	this.nome = nome;
	this.email = email;
	
  }

  
}
