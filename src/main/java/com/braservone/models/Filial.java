package com.braservone.models;

import jakarta.persistence.*;

@Entity
@Table(name = "filial")
public class Filial {

    @Id
    private Long codFilial;

    @Column(nullable = false)
    private String nome;

    @Embedded
    private Endereco endereco;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;


    public Endereco getEndereco() { return endereco; }
    
    public Long getCodFilial() {
		return codFilial;
	}
	public void setCodFilial(Long codFilial) {
		this.codFilial = codFilial;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}
