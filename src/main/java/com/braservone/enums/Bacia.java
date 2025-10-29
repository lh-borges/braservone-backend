package com.braservone.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.text.Normalizer;

public enum Bacia {
	DESCONHECIDO,
    ACRE,
    ALAGOAS,
    ALMADA,
    AMAZONAS,
    ARARIPE,
    BARREIRINHAS,
    BRAGANCA_VIZEU,
    CAMAMU,
    CAMPOS,
    CEARA,
    CUMURUXATIBA,
    ESPIRITO_SANTO,
    FOZ_DO_AMAZONAS,
    JACUIPE,
    JATOBA,
    JEQUITINHONHA,
    MARAJO,
    MUCURI,
    PANTANAL,
    PARANA,
    PARA_MARANHAO,
    PARECIS_ALTO_XINGU,
    PARNAIBA,
    PELOTAS,
    PERNAMBUCO_PARAIBA,
    POTIGUAR,
    RECONCAVO,
    RIO_DO_PEIXE,
    SANTOS,
    SAO_FRANCISCO,
    SAO_LUIS,
    SERGIPE,
    SOLIMOES,
    TACUTU,
    TUCANO_CENTRAL,
    TUCANO_NORTE,
    TUCANO_SUL;

    @JsonCreator
    public static Bacia fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;

        // remove acentos
        String s = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // upper + troca separadores por "_"
        s = s.toUpperCase()
             .replaceAll("[^A-Z0-9]+", "_")   // espaços, hífens, barras, etc. -> _
             .replaceAll("_+", "_")           // colapsa múltiplos
             .replaceAll("^_|_$", "");        // tira underscores nas bordas

        // aliases comuns
        // (adicione aqui se o dataset trouxer outras formas)
        if (s.equals("BRAGANCA_VIZEU") || s.equals("BRAGANCA__VIZEU")) s = "BRAGANCA_VIZEU";
        if (s.equals("PARA_MARANHAO") || s.equals("PARA__MARANHAO")) s = "PARA_MARANHAO";
        if (s.equals("PARECIS") || s.equals("ALTO_XINGU") || s.equals("PARECIS_ALTO_XINGU")) s = "PARECIS_ALTO_XINGU";
        if (s.equals("PERNAMBUCO_PARAIBA") || s.equals("PERNAMBUCO__PARAIBA")) s = "PERNAMBUCO_PARAIBA";

        try {
            return Bacia.valueOf(s);
        } catch (IllegalArgumentException ex) {
            // como fallback seguro, retorne null (validado pela sua camada de serviço) ou lance uma exceção clara
            return null;
        }
    }
}
