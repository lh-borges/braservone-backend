package com.braservone.DTO;

import com.braservone.enums.Setor;
import com.braservone.enums.StatusReporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReporteCreateDTO(
        @NotBlank String mensagem,
        @NotBlank String matricula,
        @NotNull Setor setor,
        StatusReporte status,   // opcional (service usa NOVO se null)
        String veiculoPlaca     // opcional
) {}
