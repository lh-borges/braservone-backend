package com.braservone.DTO;

import java.math.BigDecimal;

import com.braservone.enums.Estado;
import com.braservone.enums.TipoQuimico;

public class EstoqueQuimicoPorTipoRegiaoDTO {
    private final TipoQuimico tipoQuimico;
    private final Estado estadoLocalArmazenamento; // RN, AL, etc.
    private final BigDecimal estoqueAtual;         // soma do saldo

    public EstoqueQuimicoPorTipoRegiaoDTO(
            TipoQuimico tipoQuimico,
            Estado estadoLocalArmazenamento,
            BigDecimal estoqueAtual) {
        this.tipoQuimico = tipoQuimico;
        this.estadoLocalArmazenamento = estadoLocalArmazenamento;
        this.estoqueAtual = estoqueAtual;
    }

    public TipoQuimico getTipoQuimico() { return tipoQuimico; }
    public Estado getEstadoLocalArmazenamento() { return estadoLocalArmazenamento; }
    public BigDecimal getEstoqueAtual() { return estoqueAtual; }
}
