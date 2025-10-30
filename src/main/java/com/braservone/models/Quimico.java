package com.braservone.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.braservone.enums.Estado;
import com.braservone.enums.StatusQuimicos;
import com.braservone.enums.TipoQuimico;
import com.braservone.enums.UnidadeMedida;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

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

    // 🆕 onde o químico está fisicamente (estado)
    @Enumerated(EnumType.STRING)
    private Estado estadoLocalArmazenamento;

    // 🆕 observações livres
    @Column(length = 500)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidade;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal estoqueInicial = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusQuimicos statusQuimicos;

    private OffsetDateTime dataCompra;

    // --- PATCH helper: atualiza apenas campos não nulos ---
    public void atualizarCom(Quimico p) {
        if (p == null) return;
        if (p.tipoQuimico != null) this.tipoQuimico = p.tipoQuimico;
        if (p.fornecedor != null) this.fornecedor = p.fornecedor;
        if (p.lote != null) this.lote = p.lote;
        if (p.valorQuimico != null) this.valorQuimico = p.valorQuimico;
        if (p.unidade != null) this.unidade = p.unidade;
        if (p.estoqueInicial != null) this.estoqueInicial = p.estoqueInicial;
        if (p.statusQuimicos != null) this.statusQuimicos = p.statusQuimicos;
        if (p.dataCompra != null) this.dataCompra = p.dataCompra;
        // 🆕 novos campos:
        if (p.estadoLocalArmazenamento != null) this.estadoLocalArmazenamento = p.estadoLocalArmazenamento;
        if (p.observacao != null) this.observacao = p.observacao;
    }

    // ===== getters/setters =====

    public Long getCodigo() {
        return codigo;
    }
    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public TipoQuimico getTipoQuimico() {
        return tipoQuimico;
    }
    public void setTipoQuimico(TipoQuimico tipoQuimico) {
        this.tipoQuimico = tipoQuimico;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }
    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getLote() {
        return lote;
    }
    public void setLote(String lote) {
        this.lote = lote;
    }

    public BigDecimal getValorQuimico() {
        return valorQuimico;
    }
    public void setValorQuimico(BigDecimal valorQuimico) {
        this.valorQuimico = valorQuimico;
    }

    public Estado getEstadoLocalArmazenamento() {
        return estadoLocalArmazenamento;
    }
    public void setEstadoLocalArmazenamento(Estado estadoLocalArmazenamento) {
        this.estadoLocalArmazenamento = estadoLocalArmazenamento;
    }

    public String getObservacao() {
        return observacao;
    }
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public UnidadeMedida getUnidade() {
        return unidade;
    }
    public void setUnidade(UnidadeMedida unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getEstoqueInicial() {
        return estoqueInicial;
    }
    public void setEstoqueInicial(BigDecimal estoqueInicial) {
        this.estoqueInicial = estoqueInicial;
    }

    public StatusQuimicos getStatusQuimicos() {
        return statusQuimicos;
    }
    public void setStatusQuimicos(StatusQuimicos statusQuimicos) {
        this.statusQuimicos = statusQuimicos;
    }

    public OffsetDateTime getDataCompra() {
        return dataCompra;
    }
    public void setDataCompra(OffsetDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }
}
