package com.braservone.mapper;

import com.braservone.DTO.FornecedorLite;
import com.braservone.DTO.QuimicoDTO;
import com.braservone.models.Quimico;

public final class QuimicoMapper {

    private QuimicoMapper() {}

    public static QuimicoDTO toDTO(Quimico q) {
        if (q == null) return null;

        // fornecedor enxuto
        FornecedorLite f = (q.getFornecedor() == null)
                ? null
                : new FornecedorLite(q.getFornecedor().getId(), q.getFornecedor().getNome());

        return new QuimicoDTO(
                q.getCodigo(),
                q.getTipoQuimico() != null ? q.getTipoQuimico().name() : null,
                f,
                q.getLote(),
                q.getUnidade() != null ? q.getUnidade().name() : null,
                q.getEstoqueInicial(),
                q.getValorQuimico(),
                q.getDataCompra(),
                q.getStatusQuimicos() != null ? q.getStatusQuimicos().name() : null,
                // 🆕 campos novos:
                q.getEstadoLocalArmazenamento(),  // enum já vai direto
                q.getObservacao()
        );
    }
}
