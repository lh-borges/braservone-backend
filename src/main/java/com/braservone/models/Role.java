package com.braservone.models;

import java.util.Objects;

import com.braservone.enums.ERole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
  name = "roles",
  uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(name = "name", length = 30, nullable = false, unique = true)
  private ERole name;

  // --- construtores
  protected Role() { }                 // JPA
  public Role(ERole name) { this.name = name; }

  // --- fábrica simples (opcional, deixa seed mais legível)
  public static Role of(ERole name) { return new Role(name); }

  // --- getters/setters
  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }

  public ERole getName() { return name; }
  public void setName(ERole name) { this.name = name; }

  // --- equals/hashCode:
  // usa id se já existe; caso contrário, cai para o enum (único)
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role)) return false;
    Role other = (Role) o;
    if (id != null && other.id != null) return Objects.equals(id, other.id);
    return name == other.name;
  }

  @Override
  public int hashCode() {
    return (id != null) ? Objects.hash(id) : Objects.hash(name);
  }

  @Override
  public String toString() {
    return "Role{id=" + id + ", name=" + name + '}';
  }
}
