package com.braservone.DTO;

import java.math.BigDecimal;

import com.braservone.enums.Bacia;
import com.braservone.enums.Estado;
import com.braservone.enums.StatusPoco;
import com.braservone.models.Poco;

public class PocoDTO {

  private String codANP;
  private Bacia bacia;
  private Estado estado;
  private StatusPoco status;   // <-- ; corrigido

  // Campos usados no toDTO do service
  private String fluido;
  private String nomeCampo;
  private String local;
  private BigDecimal latitude;
  private BigDecimal longitude;

  // Getters/Setters
  public String getCodANP() {
    return codANP;
  }
  public void setCodANP(String codANP) {
    this.codANP = codANP;
  }

  public Bacia getBacia() {
    return bacia;
  }
  public void setBacia(Bacia bacia) {
    this.bacia = bacia;
  }

  public Estado getEstado() {
    return estado;
  }
  public void setEstado(Estado estado) {
    this.estado = estado;
  }

  public StatusPoco getStatus() {
    return status;
  }
  public void setStatus(StatusPoco status) {
    this.status = status;
  }

  public String getFluido() {
    return fluido;
  }
  public void setFluido(String fluido) {
    this.fluido = fluido;
  }

  public String getNomeCampo() {
    return nomeCampo;
  }
  public void setNomeCampo(String nomeCampo) {
    this.nomeCampo = nomeCampo;
  }

  public String getLocal() {
    return local;
  }
  public void setLocal(String local) {
    this.local = local;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }
  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }
  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }
}
