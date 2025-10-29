// com/projetopetroleo/dto/OperacaoCreateDTO.java
package com.braservone.DTO;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OperacaoCreateDTO(
  @NotBlank String pocoCodigoAnp,
  @NotNull  Long operadoraId,
  @NotBlank String nomeOperacao,
  @NotNull  Boolean status,
  LocalDateTime dataInicio,
  LocalDateTime dataFinal
) {}
