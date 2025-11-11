package com.braservone.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.braservone.enums.TipoMovimento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quimico_movimento")
public class QuimicoMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> POCO(codigo_anp)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poco_codigo_anp", nullable = false)
    private Poco poco;

    // FK -> QUIMICO(codigo)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quimico_codigo", nullable = false)
    private Quimico quimico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false, length = 20)
    private TipoMovimento tipoMovimento;

    @Column(name = "qnt_movimentada", nullable = false, precision = 18, scale = 6)
    private BigDecimal qntMovimentada;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    // Só para o front consumir sem quebrar o mapping
    @Transient
    public String getPocoCodigoAnp() {
        return poco != null ? poco.getCodigoAnp() : null;
    }
}
