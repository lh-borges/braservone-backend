package com.braservone.models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Cimentacao {
	
	@Id
	private Long id;
	
	@OneToMany
	private List<Quimico> quimicos;
	@OneToOne
	private Tampao tampao;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Quimico> getQuimicos() {
		return quimicos;
	}
	public void setQuimicos(List<Quimico> quimicos) {
		this.quimicos = quimicos;
	}
	public Tampao getTampao() {
		return tampao;
	}
	public void setTampao(Tampao tampao) {
		this.tampao = tampao;
	}
	
	
	
}
