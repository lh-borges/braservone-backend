package com.braservone.DTO;

import java.math.BigDecimal;

import com.braservone.enums.TipoMovimento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record RegistrarMovimentoRequest(
    @NotNull Long quimicoCodigo,
    String pocoId,               // se você registrar por PK do poço
    String pocoCodigoAnp,      // ou por código ANP do poço
    @NotNull TipoMovimento tipo,
    @NotNull @DecimalMin("0.000001") BigDecimal quantidade
) {}
