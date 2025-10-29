package com.braservone.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.text.Normalizer;

public enum StatusPoco {
  ABANDONADO_AGUARDANDO_ABANDONO_DEFINITIVO_ARRASAMENTO,
  ABANDONADO_PERMANENTEMENTE,
  ABANDONADO_POR_LOGISTICA_EXPLORATORIA,
  ABANDONADO_TEMPORARIAMENTE_COM_MONITORAMENTO,
  ABANDONADO_TEMPORARIAMENTE_SEM_MONITORAMENTO,
  ABANDONADO_PARADO_AGUARDANDO_INTERVENCAO_PARA_AVALIACAO_COMPLETACAO_OU_RESTAURACAO,
  ARRASADO,
  CEDIDO_PARA_CAPTACAO_DE_AGUA,
  DEVOLVIDO,
  EM_AVALIACAO,
  EM_COMPLETACAO,
  EM_INTERVENCAO,
  EM_OBSERVACAO,
  EM_PERFURACAO,
  EQUIPADO_AGUARDANDO_INICIO_DE_OPERACAO,
  EQUIPADO_AGUARDANDO_INICIO_DE_PRODUCAO,
  FECHADO,
  INJETANDO,
  OPERANDO_PARA_CAPTACAO_DE_AGUA,
  OPERANDO_PARA_DESCARTE,
  OUTRO,
  PRODUZINDO,
  PRODUZINDO_E_INJETANDO;

  @JsonCreator
  public static StatusPoco fromJson(Object raw) {
    if (raw == null) return null;
    String s = normalize(raw.toString());

    // Placeholder comum vindo do front
    if (s.isBlank() || "STATUS".equals(s)) return null;

    // Aliases extras? mapeie aqui se precisar:
    // if ("PRODUZINDO_E_INJET".equals(s)) s = "PRODUZINDO_E_INJETANDO";

    try {
      return StatusPoco.valueOf(s);
    } catch (IllegalArgumentException e) {
      // Desconhecido -> trate como null (campo opcional)
      return null;
    }
  }

  private static String normalize(String in) {
    String s = in == null ? "" : in.trim();
    s = Normalizer.normalize(s, Normalizer.Form.NFD)
         .replaceAll("\\p{M}", "");         // remove acentos
    s = s.toUpperCase()
         .replaceAll("[^A-Z0-9]+", "_")     // separadores -> _
         .replaceAll("^_+|_+$", "")         // tira _ nas bordas
         .replaceAll("_+", "_");            // colapsa __
    return s;
  }
}
