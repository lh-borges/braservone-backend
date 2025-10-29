// QuimicoMovimento.java
package com.braservone.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import com.braservone.enums.TipoMovimento;

@Entity
public class QuimicoMovimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitos movimentos para um poço
    @ManyToOne(optional = false)
    @JoinColumn(name = "poco_codigo_anp")   // PK do Poco é codigo_anp (String)
    private Poco poco;

    // Muitos movimentos para um químico

@ManyToOne(optional = false)
@JoinColumn(name = "quimico_codigo")    // PK do Quimico é codigo (Long)
private Quimico quimico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimento tipoMovimento; // ENTRADA | SAIDA

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal qntMovimentada;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Poco getPoco() {
		return poco;
	}

	public void setPoco(Poco poco) {
		this.poco = poco;
	}

	public Quimico getQuimico() {
		return quimico;
	}

	public void setQuimico(Quimico quimico) {
		this.quimico = quimico;
	}

	public TipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}

	public void setTipoMovimento(TipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public BigDecimal getQntMovimentada() {
		return qntMovimentada;
	}

	public void setQntMovimentada(BigDecimal qntMovimentada) {
		this.qntMovimentada = qntMovimentada;
	}

	public OffsetDateTime getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(OffsetDateTime criadoEm) {
		this.criadoEm = criadoEm;
	}

    
}
