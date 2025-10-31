package com.braservone.payload.response;

import java.util.List;

// REMOVIDO: import com.braservone.DTO.EmpresaDTO;
// REMOVIDO: import com.braservone.models.Empresa;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String username;
  private String email;
  // REMOVIDO: private EmpresaDTO empresa; // Campo EmpresaDTO removido

  private List<String> roles;

  // Construtor usado no /me (sem refresh token)
  public JwtResponse(String accessToken, String username, String email, List<String> roles) {
    this.token = accessToken;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }

  // Construtor usado no /login (com refresh token)
  public JwtResponse(String accessToken, String refreshToken, String username, String email, List<String> roles) {
	  this.token = accessToken;
	  // Nota: o refreshToken não é um campo do DTO, mas foi passado aqui para reuso.
	  // Se você deseja retorná-lo no DTO, adicione um campo para ele.
	  this.username = username;
	  this.email = email;
	  this.roles = roles;
  }

  // Getters e Setters

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

  // REMOVIDO: Getters e setters de Empresa

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
