package com.braservone.models;

import com.braservone.enums.TipoFornecedor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
// evita "hibernateLazyInitializer"/"handler" no JSON, mesmo se Hibernate usar proxy
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoFornecedor tipo;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoFornecedor getTipo() { return tipo; }
    public void setTipo(TipoFornecedor tipo) { this.tipo = tipo; }
}
