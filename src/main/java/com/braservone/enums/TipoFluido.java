package com.braservone.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.text.Normalizer;

public enum TipoFluido {
  OLEO_LEVE,
  OLEO_MEDIO,
  OLEO_PESADO,
  OLEO_EXTRA_PESADO,
  GAS_NATURAL,
  CONDENSADO,
  MISTO;

  @JsonCreator
  public static TipoFluido fromJson(Object raw) {
    if (raw == null) return null;
    String s = normalize(raw.toString());
    if (s.isBlank() || "FLUIDO".equals(s)) return null; // placeholder
    try {
      return TipoFluido.valueOf(s);
    } catch (IllegalArgumentException e) {
      return null; // trata desconhecido como null (campo opcional, se quiser)
    }
  }

  private static String normalize(String in) {
    String s = in == null ? "" : in.trim();
    s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    s = s.toUpperCase()
         .replaceAll("[^A-Z0-9]+", "_")
         .replaceAll("^_+|_+$", "")
         .replaceAll("_+", "_");
    return s;
  }
}
