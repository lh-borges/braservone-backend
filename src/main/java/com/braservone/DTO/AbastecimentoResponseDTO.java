// src/main/java/com/braservone/DTO/AbastecimentoResponseDTO.java
package com.braservone.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.braservone.models.Abastecimento;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AbastecimentoResponseDTO {
    private Long id;
    private String placaVeiculo;

    private BigDecimal distRodadaKm;
    private BigDecimal volumeLitros;
    private BigDecimal valorTotal;
    private BigDecimal valorPorLitro;

    private BigDecimal mediaKmPorL;
    private BigDecimal mediaRsPorKm;

    private LocalDateTime dataAbastecimento;


    public static AbastecimentoResponseDTO toResponse(Abastecimento a) {
        if (a == null) return null;
        AbastecimentoResponseDTO r = new AbastecimentoResponseDTO();
        r.setId(a.getId());
        r.setPlacaVeiculo(a.getVeiculo() != null ? a.getVeiculo().getPlaca() : null);
        r.setDistRodadaKm(a.getDistRodadaKm());
        r.setVolumeLitros(a.getVolumeLitros());
        r.setValorTotal(a.getValorTotal());
        r.setValorPorLitro(a.getValorPorLitro());
        r.setMediaKmPorL(a.getMediaKmPorL());
        r.setMediaRsPorKm(a.getMediaRsPorKm());
        r.setDataAbastecimento(a.getDataAbastecimento());
        return r;
    }
}
