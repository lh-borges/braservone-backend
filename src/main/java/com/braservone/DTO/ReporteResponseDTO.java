package com.braservone.DTO;

import java.time.LocalDateTime;
import com.braservone.enums.Setor;
import com.braservone.enums.StatusReporte;

public record ReporteResponseDTO(
        Long id,
        String mensagem,
        String matricula,
        Setor setor,
        StatusReporte status,
        String veiculoPlaca,
        LocalDateTime dataHoraReporte
) {}
