package com.braservone.DTO;

import java.math.BigDecimal;

import com.braservone.enums.TipoMovimento;

public record QuimicoMovimentoCreateDTO(
    String pocoCodigoAnp,
    Long quimicoCodigo,
    TipoMovimento tipoMovimento,
    BigDecimal qntMovimentada
) {}