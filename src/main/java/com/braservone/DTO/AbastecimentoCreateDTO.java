// com.projetopetroleo.DTO.AbastecimentoCreateDTO
package com.braservone.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AbastecimentoCreateDTO {

    @NotBlank
    private String placaVeiculo;

    @NotNull @PositiveOrZero
    @Digits(integer = 16, fraction = 2)
    private BigDecimal distRodadaKm;

    @NotNull @PositiveOrZero
    @Digits(integer = 16, fraction = 3)
    private BigDecimal volumeLitros;

    // Pelo menos um dos dois deve vir (o Service valida)
    @PositiveOrZero
    @Digits(integer = 16, fraction = 2)
    private BigDecimal valorTotal;

    @PositiveOrZero
    @Digits(integer = 16, fraction = 3)
    private BigDecimal valorPorLitro;

    // opcionais: se não vierem, a entidade calcula no @PrePersist
    @PositiveOrZero
    @Digits(integer = 16, fraction = 6)
    private BigDecimal mediaKmPorL;

    @PositiveOrZero
    @Digits(integer = 16, fraction = 6)
    private BigDecimal mediaRsPorKm;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataAbastecimento;

    // getters/setters
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
