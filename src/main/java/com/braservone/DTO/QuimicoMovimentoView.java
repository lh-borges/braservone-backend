package com.braservone.DTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface QuimicoMovimentoView {

  // Movimento
  Long getId();
  String getTipoMovimento();
  BigDecimal getQntMovimentada();
  OffsetDateTime getCriadoEm();

  // Poço
  String getPocoCodigoAnp();
  String getPocoTipo();
  String getPocoEstado();
  String getPocoAmbiente();

  // Químico
  Long getQuimicoCodigo();
  String getQuimicoTipo();
  String getQuimicoUnidade();
  BigDecimal getQuimicoValor();
  BigDecimal getQuimicoEstoqueInicial();
  String getQuimicoStatus();
  OffsetDateTime getQuimicoDataCompra();

  // Fornecedor
  Long getFornecedorId();
  String getFornecedorNome();
  String getFornecedorTipo();
}
