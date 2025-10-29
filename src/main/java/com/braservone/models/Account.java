package com.braservone.models;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

// com.projetopetroleo.models.Account
@Entity
@Table(name = "accounts")
public class Account {
	@Id
	@Column(name = "username", length = 20, nullable = false)
	private String username;

	@MapsId
	@OneToOne(optional = false)
	@JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
	private User user;

  @JsonIgnore
  @Column(length = 120, nullable = false)
  private String password;

  @Column(nullable = false)
  private boolean enabled = true;

  @ManyToMany(fetch = FetchType.LAZY)
  private Set<Role> roles = new HashSet<>();

  // helpers:
  public void changePassword(String encoded) { this.password = encoded; }

  public String getUsername() {
	return username;
  }

  public void setUsername(String username) {
	this.username = username;
  }

  public User getUser() {
	return user;
  }

  public void setUser(User user) {
	this.user = user;
  }

  public String getPassword() {
	return password;
  }

  public void setPassword(String password) {
	this.password = password;
  }

  public boolean isEnabled() {
	return enabled;
  }

  public void setEnabled(boolean enabled) {
	this.enabled = enabled;
  }

  public Set<Role> getRoles() {
	return roles;
  }

  public void setRoles(Set<Role> roles) {
	this.roles = roles;
  }
  
  
}
