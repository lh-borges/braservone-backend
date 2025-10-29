// com/projetopetroleo/dto/OperacaoResponseDTO.java
package com.braservone.DTO;

import java.time.LocalDateTime;

public record OperacaoResponseDTO(
  Long id,
  String pocoCodigoAnp,
  String nomeOperacao,
  Long operadoraId,
  String operadoraNome,
  boolean status,
  LocalDateTime dataInicio,
  LocalDateTime dataFinal
) {}
