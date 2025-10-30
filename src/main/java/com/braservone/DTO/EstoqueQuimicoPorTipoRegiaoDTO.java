package com.braservone.DTO;

import java.math.BigDecimal;

public record EstoqueQuimicoPorTipoRegiaoDTO(
    String tipoQuimico,
    String estadoLocalArmazenamento,
    BigDecimal estoqueTotal
) {}