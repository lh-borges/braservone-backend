// com/braservone/DTO/PocoDTO.java
package com.braservone.DTO;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.braservone.enums.Bacia;
import com.braservone.enums.Estado;
import com.braservone.enums.StatusPoco;
import com.braservone.models.Poco;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PocoDTO {

  private String codANP;
  private Bacia bacia;
  private Estado estado;
  private StatusPoco status;

  // Campos usados no toDTO do service
  private String fluido;
  private String nomeCampo;
  private String local;
  private BigDecimal latitude;
  private BigDecimal longitude;

  /** Fábrica: monta o DTO a partir da entidade sem acessar recursos externos. */
  public static PocoDTO fromEntity(Poco p) {
    if (p == null) return null;
    PocoDTO dto = new PocoDTO();
    dto.setCodANP(p.getCodigoAnp());
    dto.setBacia(p.getBacia());
    // dto.setEstado(p.getEstado()); // habilite se a entidade tiver este campo
    dto.setStatus(p.getStatus());
    dto.setFluido(p.getFluido() != null ? p.getFluido().name() : null);
    dto.setNomeCampo(p.getNomeCampo());
    dto.setLocal(p.getLocal());
    dto.setLatitude(p.getLatitude());
    dto.setLongitude(p.getLongitude());
    return dto;
  }

  /** Validação simples que o próprio DTO consegue fazer. */
  public void validateCodigoAnp() {
    if (!StringUtils.hasText(this.codANP)) {
      throw new IllegalArgumentException("codigoAnp é obrigatório.");
    }
  }
}
