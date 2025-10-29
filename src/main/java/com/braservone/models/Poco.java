package com.braservone.models;

import java.math.BigDecimal;

import com.braservone.enums.Bacia;
import com.braservone.enums.StatusPoco;
import com.braservone.enums.TipoFluido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

@Entity
@Table(
  name = "pocos",
  indexes = {
    @Index(name = "idx_poco_status", columnList = "status"),
    @Index(name = "idx_poco_bacia", columnList = "bacia"),
    @Index(name = "idx_poco_nome_campo", columnList = "nome_campo")
  }
)
public class Poco {

  @Id
  @Column(name = "codigo_anp", nullable = false, length = 64, unique = true)
  private String codigoAnp;

  @Size(max = 120)
  @Column(name = "nome_campo", length = 120)
  private String nomeCampo;

  @Enumerated(EnumType.STRING)
  @Column(name = "bacia", length = 64, nullable = true)
  private Bacia bacia;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 48, nullable = true)
  private StatusPoco status;

  @Enumerated(EnumType.STRING)
  @Column(name = "fluido", length = 32)
  private TipoFluido fluido;

  // "local" é útil pro usuário; no banco uso "localidade" para evitar colisões.
  @Size(max = 120)
  @Column(name = "localidade", length = 120)
  private String local;

  // Precisão típica para coordenadas: ~6 casas decimais
  @DecimalMin(value = "-90.0")
  @DecimalMax(value = "90.0")
  @Column(name = "latitude", precision = 15, scale = 6)
  private BigDecimal latitude;

  @DecimalMin(value = "-180.0")
  @DecimalMax(value = "180.0")
  @Column(name = "longitude", precision = 15, scale = 6)
  private BigDecimal longitude;

  // === Getters/Setters ===
  public String getCodigoAnp() { return codigoAnp; }
  public void setCodigoAnp(String codigoAnp) { this.codigoAnp = codigoAnp; }

  public String getNomeCampo() { return nomeCampo; }
  public void setNomeCampo(String nomeCampo) { this.nomeCampo = nomeCampo; }

  public Bacia getBacia() { return bacia; }
  public void setBacia(Bacia bacia) { this.bacia = bacia; }

  public StatusPoco getStatus() { return status; }
  public void setStatus(StatusPoco status) { this.status = status; }

  public TipoFluido getFluido() { return fluido; }
  public void setFluido(TipoFluido fluido) { this.fluido = fluido; }

  public String getLocal() { return local; }
  public void setLocal(String local) { this.local = local; }

  public BigDecimal getLatitude() { return latitude; }
  public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

  public BigDecimal getLongitude() { return longitude; }
  public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

  // === Atualização Parcial (ignora null/blank) ===
  public void atualizar(Poco src) {
    if (src == null) return;

    if (notBlank(src.getNomeCampo())) this.nomeCampo = src.getNomeCampo();
    if (src.getBacia() != null)        this.bacia = src.getBacia();
    if (src.getStatus() != null)       this.status = src.getStatus();
    if (src.getFluido() != null)       this.fluido = src.getFluido();
    if (notBlank(src.getLocal()))      this.local = src.getLocal();
    if (src.getLatitude() != null)     this.latitude = src.getLatitude();
    if (src.getLongitude() != null)    this.longitude = src.getLongitude();
    // codigoAnp é ID — não deve ser atualizado aqui.
  }

  private boolean notBlank(String s) { return s != null && !s.trim().isEmpty(); }

  // equals/hashCode por ID para coleções e cache de primeiro nível
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Poco)) return false;
    Poco other = (Poco) o;
    return codigoAnp != null && codigoAnp.equals(other.codigoAnp);
  }

  @Override
  public int hashCode() {
    return codigoAnp != null ? codigoAnp.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Poco{" +
      "codigoAnp='" + codigoAnp + '\'' +
      ", nomeCampo='" + nomeCampo + '\'' +
      ", bacia=" + bacia +
      ", status=" + status +
      ", fluido=" + fluido +
      ", local='" + local + '\'' +
      ", latitude=" + latitude +
      ", longitude=" + longitude +
      '}';
  }
}
