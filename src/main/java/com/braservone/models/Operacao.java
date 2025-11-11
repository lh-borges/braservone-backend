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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "operacao")
public class Operacao {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(
      name = "poco_codigo_anp",               
      referencedColumnName = "codigo_anp",   
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
  
  
}
