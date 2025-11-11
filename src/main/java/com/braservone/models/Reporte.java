package com.braservone.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.braservone.enums.Setor;
import com.braservone.enums.StatusReporte;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "reporte")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String mensagem;

    @NotBlank
    private String matricula;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Setor setor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReporte status = StatusReporte.NOVO;

    /**
     * Associação OPCIONAL com Veículo.
     * Assumindo que a PK de Veiculo é a PLACA (String). Se for Long id, ajuste o referencedColumnName.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
        name = "veiculo_placa",
        referencedColumnName = "placa",
        nullable = true
    )
    private Veiculo veiculo;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reporte_id")
    private List<Observacao> listObservacoes = new ArrayList<>();

    private LocalDateTime dataHoraReporte;

    @PrePersist
    void prePersist() {
        if (status == null) status = StatusReporte.NOVO;
        if (dataHoraReporte == null) dataHoraReporte = LocalDateTime.now();
    }
}
