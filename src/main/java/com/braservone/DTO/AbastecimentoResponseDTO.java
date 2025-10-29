// com.projetopetroleo.DTO.AbastecimentoResponseDTO
package com.braservone.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlacaVeiculo() { return placaVeiculo; }
    public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }
    public BigDecimal getDistRodadaKm() { return distRodadaKm; }
    public void setDistRodadaKm(BigDecimal distRodadaKm) { this.distRodadaKm = distRodadaKm; }
    public BigDecimal getVolumeLitros() { return volumeLitros; }
    public void setVolumeLitros(BigDecimal volumeLitros) { this.volumeLitros = volumeLitros; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public BigDecimal getValorPorLitro() { return valorPorLitro; }
    public void setValorPorLitro(BigDecimal valorPorLitro) { this.valorPorLitro = valorPorLitro; }
    public BigDecimal getMediaKmPorL() { return mediaKmPorL; }
    public void setMediaKmPorL(BigDecimal mediaKmPorL) { this.mediaKmPorL = mediaKmPorL; }
    public BigDecimal getMediaRsPorKm() { return mediaRsPorKm; }
    public void setMediaRsPorKm(BigDecimal mediaRsPorKm) { this.mediaRsPorKm = mediaRsPorKm; }
    public LocalDateTime getDataAbastecimento() { return dataAbastecimento; }
    public void setDataAbastecimento(LocalDateTime dataAbastecimento) { this.dataAbastecimento = dataAbastecimento; }
}
