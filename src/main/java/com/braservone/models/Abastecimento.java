package com.braservone.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "abastecimentos")
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PLACA (chave estrangeira)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "placa_veiculo", referencedColumnName = "placa", nullable = false)
    private Veiculo veiculo;

    // DIST. RODADA (km do ciclo)
    @NotNull
    @PositiveOrZero
    @Digits(integer = 16, fraction = 2)
    @Column(name = "dist_rodada_km", precision = 18, scale = 2, nullable = false)
    private BigDecimal distRodadaKm;

    // VOLUME TOTAL (L)
    @NotNull
    @PositiveOrZero
    @Digits(integer = 16, fraction = 3)
    @Column(name = "volume_litros", precision = 18, scale = 3, nullable = false)
    private BigDecimal volumeLitros;

    // VALOR TOTAL (R$)
    @NotNull
    @PositiveOrZero
    @Digits(integer = 16, fraction = 2)
    @Column(name = "valor_total", precision = 18, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    // VALOR POR LITRO (R$/L) – no BR costuma ter 3 casas
    @NotNull
    @PositiveOrZero
    @Digits(integer = 16, fraction = 3)
    @Column(name = "valor_por_litro", precision = 18, scale = 3, nullable = false)
    private BigDecimal valorPorLitro;

    // MÉDIA KM/L (distância / volume)
    @PositiveOrZero
    @Digits(integer = 16, fraction = 6)
    @Column(name = "media_km_por_l", precision = 24, scale = 6)
    private BigDecimal mediaKmPorL;

    // MÉDIA R$/KM (valor / distância)
    @PositiveOrZero
    @Digits(integer = 16, fraction = 6)
    @Column(name = "media_rs_por_km", precision = 24, scale = 6)
    private BigDecimal mediaRsPorKm;

    // DATA ABASTECIMENTO
    @NotNull
    @Column(name = "data_abastecimento", nullable = false)
    private LocalDateTime dataAbastecimento;

    /* ----------------- Cálculo/Normalização ----------------- */

    @PrePersist
    @PreUpdate
    private void normalizeAndCompute() {
        // arredondamentos consistentes
        distRodadaKm  = setScale(distRodadaKm, 2);
        volumeLitros  = setScale(volumeLitros, 3);
        valorTotal    = setScale(valorTotal, 2);
        valorPorLitro = setScale(valorPorLitro, 3);
        mediaKmPorL   = setScale(mediaKmPorL, 6);
        mediaRsPorKm  = setScale(mediaRsPorKm, 6);

        // garante coerência básica: se 2 de 3 valores existirem, calcula o 3º
        if (isPositive(volumeLitros) && isPositive(valorPorLitro) && valorTotal == null) {
            valorTotal = setScale(valorPorLitro.multiply(volumeLitros), 2);
        } else if (isPositive(valorTotal) && isPositive(volumeLitros) && valorPorLitro == null && volumeLitros.signum() != 0) {
            valorPorLitro = setScale(valorTotal.divide(volumeLitros, 6, RoundingMode.HALF_UP), 3);
        }

        // médias
        if (isPositive(distRodadaKm) && isPositive(volumeLitros) && volumeLitros.signum() != 0) {
            mediaKmPorL = setScale(distRodadaKm.divide(volumeLitros, 6, RoundingMode.HALF_UP), 6);
        }
        if (isPositive(valorTotal) && isPositive(distRodadaKm) && distRodadaKm.signum() != 0) {
            mediaRsPorKm = setScale(valorTotal.divide(distRodadaKm, 6, RoundingMode.HALF_UP), 6);
        }
    }

    private static BigDecimal setScale(BigDecimal v, int scale) {
        return v == null ? null : v.setScale(scale, RoundingMode.HALF_UP);
    }
    private static boolean isPositive(BigDecimal v) {
        return v != null && v.signum() >= 0;
    }

    /* ----------------- Getters/Setters ----------------- */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

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

    /* ----------------- equals/hash/toString ----------------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Abastecimento that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Abastecimento{" +
                "id=" + id +
                ", placa=" + (veiculo != null ? veiculo.getPlaca() : null) +
                ", distRodadaKm=" + distRodadaKm +
                ", volumeLitros=" + volumeLitros +
                ", valorTotal=" + valorTotal +
                ", valorPorLitro=" + valorPorLitro +
                ", mediaKmPorL=" + mediaKmPorL +
                ", mediaRsPorKm=" + mediaRsPorKm +
                ", dataAbastecimento=" + dataAbastecimento +
                '}';
    }
}
