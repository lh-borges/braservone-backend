// com/projetopetroleo/dto/OperacaoUpdateDTO.java
package com.braservone.DTO;

import java.time.LocalDateTime;

// PATCH: todos opcionais
public record OperacaoUpdateDTO(
  String pocoCodigoAnp,
  Long operadoraId,
  String nomeOperacao,
  Boolean status,
  LocalDateTime dataInicio,
  LocalDateTime dataFinal
) {}
