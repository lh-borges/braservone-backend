// com/projetopetroleo/mapper/OperacaoMapper.java
package com.braservone.mapper;

import com.braservone.DTO.OperacaoResponseDTO;
import com.braservone.models.Operacao;

public class OperacaoMapper {
  public static OperacaoResponseDTO toDTO(Operacao o) {
    return new OperacaoResponseDTO(
      o.getId(),
      o.getPoco().getCodigoAnp(),
      o.getNomeOperacao(),
      o.getOperadora().getId(),
      o.getOperadora().getNome(), // ajuste se o campo for diferente
      o.isStatus(),
      o.getDataInicio(),
      o.getDataFinal()
    );
  }
}
