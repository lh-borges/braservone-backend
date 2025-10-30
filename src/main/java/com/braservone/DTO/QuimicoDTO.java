package com.braservone.DTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.braservone.enums.Estado;

public record QuimicoDTO(
    Long codigo,
    String tipoQuimico,
    FornecedorLite fornecedor,
    String lote,
    String unidade,
    BigDecimal estoqueInicial,
    BigDecimal valorQuimico,
    OffsetDateTime dataCompra,
    String statusQuimicos,
    // 🆕 campos novos:
    Estado estadoLocalArmazenamento,
    String observacao
) {}
