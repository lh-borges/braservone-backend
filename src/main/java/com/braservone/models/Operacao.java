// com/projetopetroleo/models/Operacao.java
package com.braservone.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "operacao")
public class Operacao {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "poco_codigo_anp",               // nome da coluna na tabela operacao
      referencedColumnName = "codigo_anp",    // nome da coluna PK/UK em 'pocos'
      nullable = false,
      foreignKey = @ForeignKey(name = "FK_operacao_poco")
  )
  private Poco poco;

  @Column(nullable = false, length = 200)
  private String nomeOperacao;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "operadora_id", nullable = false)
  private Operadora operadora;

  @Column(nullable = false)
  private boolean status;

  private LocalDateTime dataInicio;
  private LocalDateTime dataFinal;
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
  public String getNomeOperacao() {
	return nomeOperacao;
  }
  public void setNomeOperacao(String nomeOperacao) {
	this.nomeOperacao = nomeOperacao;
  }
  public Operadora getOperadora() {
	return operadora;
  }
  public void setOperadora(Operadora operadora) {
	this.operadora = operadora;
  }
  public boolean isStatus() {
	return status;
  }
  public void setStatus(boolean status) {
	this.status = status;
  }
  public LocalDateTime getDataInicio() {
	return dataInicio;
  }
  public void setDataInicio(LocalDateTime dataInicio) {
	this.dataInicio = dataInicio;
  }
  public LocalDateTime getDataFinal() {
	return dataFinal;
  }
  public void setDataFinal(LocalDateTime dataFinal) {
	this.dataFinal = dataFinal;
  }

  // getters/setters
  // ...
}
