// com/projetopetroleo/models/Operadora.java
package com.braservone.models;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "operadora")
public class Operadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String nome;

    @Column(length = 16)
    private String siglas;

    private String pais;

    @Embedded
    private Endereco endereco;

    private String emailContato;
    private String telefoneContato;
    private String responsaveltecnico;

    @Column(nullable=false)
    private Boolean ativo = Boolean.TRUE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSiglas() { return siglas; }
    public void setSiglas(String siglas) { this.siglas = siglas; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public String getEmailContato() { return emailContato; }
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; }

    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }

    public String getResponsaveltecnico() { return responsaveltecnico; }
    public void setResponsaveltecnico(String responsaveltecnico) { this.responsaveltecnico = responsaveltecnico; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operadora)) return false;
        Operadora that = (Operadora) o;
        return id != null && Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return 31; }
}
