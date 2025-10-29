package com.braservone.models;

import com.braservone.enums.TipoVeiculo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Unidade {
	
	private String nome;
	@Id
	private String placa;
	private String modelo;
	private String marca;
	private TipoVeiculo tipoVeiculo;
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public TipoVeiculo getTipoVeiculo() {
		return tipoVeiculo;
	}

	public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
		this.tipoVeiculo = tipoVeiculo;
	}


	
	
	
}
