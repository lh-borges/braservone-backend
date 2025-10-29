package com.braservone.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.braservone.enums.StatusVeiculos;
import com.braservone.enums.TipoVeiculo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "veiculos")
// Opcional, ajuda a evitar ruído de proxy na serialização
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Veiculo {

    @Id
    @Column(name = "placa", length = 10, nullable = false, unique = true)
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9-]{6,10}$", message = "Placa em formato inválido")
    private String placa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusVeiculos status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_veiculo", nullable = false, length = 30)
    private TipoVeiculo tipoVeiculo;

    @Column(name = "ano_veiculo")
    private Integer anoVeiculo;

    /** Relacionamento 1:N — a FK está em Abastecimento.veiculo */
    @OneToMany(mappedBy = "veiculo", fetch = FetchType.LAZY, orphanRemoval = false)
    @JsonIgnore // <-- impede Jackson de serializar (adeus loop/LAZY)
    private List<Abastecimento> abastecimentos = new ArrayList<>();

    @PrePersist @PreUpdate
    private void normalize() {
        if (placa != null) placa = placa.trim().toUpperCase();
    }

    // Getters/Setters
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public StatusVeiculos getStatus() { return status; }
    public void setStatus(StatusVeiculos status) { this.status = status; }

    public TipoVeiculo getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(TipoVeiculo tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }

    public Integer getAnoVeiculo() { return anoVeiculo; }
    public void setAnoVeiculo(Integer anoVeiculo) { this.anoVeiculo = anoVeiculo; }

    public List<Abastecimento> getAbastecimentos() { return abastecimentos; }
    public void setAbastecimentos(List<Abastecimento> abastecimentos) { this.abastecimentos = abastecimentos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Veiculo that)) return false;
        return Objects.equals(placa, that.placa);
    }
    @Override
    public int hashCode() { return Objects.hash(placa); }
}
