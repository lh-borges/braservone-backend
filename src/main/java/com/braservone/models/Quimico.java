package com.braservone.models;

import java.math.BigDecimal;
import java.time.LocalDate;             // ← use LocalDate para data simples
import java.time.OffsetDateTime;

import com.braservone.enums.Estado;
import com.braservone.enums.StatusQuimicos;
import com.braservone.enums.TipoQuimico;
import com.braservone.enums.UnidadeMedida;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Quimico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoQuimico tipoQuimico;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Fornecedor fornecedor;

    @Column(length = 100)
    private String lote;

    @Column(precision = 18, scale = 2)
    private BigDecimal valorQuimico;

    @Enumerated(EnumType.STRING)
    private Estado estadoLocalArmazenamento;

    @Column(length = 500)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidade;

    // ✅ data de validade correta
    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal estoqueInicial = BigDecimal.ZERO;

    // ✅ novo campo impactando o saldo
    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal estoqueUtilizado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusQuimicos statusQuimicos;

    private OffsetDateTime dataCompra;

    public void atualizarCom(Quimico p) {
        if (p == null) return;
        if (p.tipoQuimico != null) this.tipoQuimico = p.tipoQuimico;
        if (p.fornecedor != null)  this.fornecedor = p.fornecedor;
        if (p.lote != null)        this.lote = p.lote;
        if (p.valorQuimico != null) this.valorQuimico = p.valorQuimico;
        if (p.unidade != null)     this.unidade = p.unidade;
        if (p.estoqueInicial != null) this.estoqueInicial = p.estoqueInicial;
        if (p.statusQuimicos != null) this.statusQuimicos = p.statusQuimicos;
        if (p.dataCompra != null)  this.dataCompra = p.dataCompra;
        if (p.estadoLocalArmazenamento != null) this.estadoLocalArmazenamento = p.estadoLocalArmazenamento;
        if (p.observacao != null)  this.observacao = p.observacao;
        // 🆕 campos novos no patch:
        if (p.dataValidade != null)      this.dataValidade = p.dataValidade;
        if (p.estoqueUtilizado != null)  this.estoqueUtilizado = p.estoqueUtilizado;
    }
}
