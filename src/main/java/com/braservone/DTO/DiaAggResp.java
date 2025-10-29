// src/main/java/com/projetopetroleo/abastecimento/dto/DiaAggResp.java
package com.braservone.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiaAggResp(
    LocalDate dia,
    BigDecimal custoTotal,    // Σ valorTotal (R$)
    Double kmRodados,         // Σ distRodadaKm (km)
    Double litros,            // Σ volumeLitros (L)
    Double vlrMedioPorLitro,  // custoTotal / litros
    Double kmPorLitro,        // kmRodados / litros
    Double rsPorKm,           // custoTotal / kmRodados
    Long qtAbastecimentos     // contagem
) {}
