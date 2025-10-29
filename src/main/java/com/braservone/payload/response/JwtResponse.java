package com.braservone.payload.response;

import java.util.List;

import com.braservone.DTO.EmpresaDTO;
import com.braservone.models.Empresa;

public class JwtResponse {
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

	

	public EmpresaDTO getEmpresa() {
		return empresa;
	}

	public void setEmpresa(EmpresaDTO empresa) {
		this.empresa = empresa;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

private String token;
  private String type = "Bearer";
  private String username;
  private String email;
  private EmpresaDTO empresa;

  private List<String> roles;

  public JwtResponse(String accessToken,String username, String email, EmpresaDTO empresa, List<String> roles) {
    this.token = accessToken;

    this.username = username;
    this.email = email;
    this.roles = roles;
    this.empresa = empresa;
 
  }

  public JwtResponse(String accessToken, String refreshToken, String username2, String email2, EmpresaDTO empresaDTO,
		List<String> roles2) {
	
	  this.token = accessToken;
	  this.username = username2;
	  this.email = email2;
	  this.empresa = empresaDTO;
	  this.roles = roles2;
	  
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
