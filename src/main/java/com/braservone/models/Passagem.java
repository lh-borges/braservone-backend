package com.braservone.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Passagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id") // nome da FK (opcional, mas ajuda)
    private User user;

    private LocalDate dataSaida;
    private LocalDate dataChegada;

    // Dinheiro: use BigDecimal + precision/scale
    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cep",         column = @Column(name = "saida_cep")),
        @AttributeOverride(name = "logradouro",  column = @Column(name = "saida_logradouro")),
        @AttributeOverride(name = "numero",      column = @Column(name = "saida_numero")),
        @AttributeOverride(name = "bairro",      column = @Column(name = "saida_bairro")),
        @AttributeOverride(name = "cidade",      column = @Column(name = "saida_cidade")),
        @AttributeOverride(name = "estado",      column = @Column(name = "saida_estado"))
    })
    private Endereco localSaida;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cep",         column = @Column(name = "chegada_cep")),
        @AttributeOverride(name = "logradouro",  column = @Column(name = "chegada_logradouro")),
        @AttributeOverride(name = "numero",      column = @Column(name = "chegada_numero")),
        @AttributeOverride(name = "bairro",      column = @Column(name = "chegada_bairro")),
        @AttributeOverride(name = "cidade",      column = @Column(name = "chegada_cidade")),
        @AttributeOverride(name = "estado",      column = @Column(name = "chegada_estado"))
    })
    private Endereco localChegada;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDate dataSaida) { this.dataSaida = dataSaida; }
    public LocalDate getDataChegada() { return dataChegada; }
    public void setDataChegada(LocalDate dataChegada) { this.dataChegada = dataChegada; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public Endereco getLocalSaida() { return localSaida; }
    public void setLocalSaida(Endereco localSaida) { this.localSaida = localSaida; }
    public Endereco getLocalChegada() { return localChegada; }
    public void setLocalChegada(Endereco localChegada) { this.localChegada = localChegada; }
}
