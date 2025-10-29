package com.braservone.mapper;

import com.braservone.DTO.FornecedorLite;
import com.braservone.DTO.QuimicoDTO;
import com.braservone.models.Quimico;

public final class QuimicoMapper {
  private QuimicoMapper() {}
  public static QuimicoDTO toDTO(Quimico q) {
    FornecedorLite f = (q.getFornecedor() == null) ? null
        : new FornecedorLite(q.getFornecedor().getId(), q.getFornecedor().getNome());
    return new QuimicoDTO(
        q.getCodigo(),
        q.getTipoQuimico().name(),
        f,
        q.getLote(),
        q.getUnidade().name(),
        q.getEstoqueInicial(),
        q.getValorQuimico(),
        q.getDataCompra(),
        q.getStatusQuimicos().name()
    );
  }
}