package com.braservone.models;

import java.time.LocalDate;

import com.braservone.enums.CentroCusto;
import com.braservone.enums.Criticidade;
import com.braservone.enums.Entrega;
import com.braservone.enums.Setor;
import com.braservone.enums.Status;
import com.braservone.enums.Subsetor;
import com.braservone.enums.TipoConsumo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class ContasPagar {
	
	@Id
	public Long pedido;
	@OneToOne
	public Departamento departamento;
	public CentroCusto centroCusto;
	public Double valor;
	public LocalDate dataEmissao;
	public Criticidade criticidade;
	public Status status;
	@OneToOne
	public Unidade unidade;
	public TipoConsumo tipoConsumo;
	public Setor setor;
	public Subsetor subsetor;
	@OneToOne
	public Filial filial;
	public LocalDate dataPagamento;
	public Entrega entrega;
	public String observacao;
	
	public Long getPedido() {
		return pedido;
	}
	public void setPedido(Long pedido) {
		this.pedido = pedido;
	}
	public Departamento getDepartamento() {
		return departamento;
	}
	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}
	public CentroCusto getCentroCusto() {
		return centroCusto;
	}
	public void setCentroCusto(CentroCusto centroCusto) {
		this.centroCusto = centroCusto;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Criticidade getCriticidade() {
		return criticidade;
	}
	public void setCriticidade(Criticidade criticidade) {
		this.criticidade = criticidade;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Unidade getUnidade() {
		return unidade;
	}
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	public TipoConsumo getTipoConsumo() {
		return tipoConsumo;
	}
	public void setTipoConsumo(TipoConsumo tipoConsumo) {
		this.tipoConsumo = tipoConsumo;
	}
	public Setor getSetor() {
		return setor;
	}
	public void setSetor(Setor setor) {
		this.setor = setor;
	}
	public Subsetor getSubsetor() {
		return subsetor;
	}
	public void setSubsetor(Subsetor subsetor) {
		this.subsetor = subsetor;
	}
	public Filial getFilial() {
		return filial;
	}
	public void setFilial(Filial filial) {
		this.filial = filial;
	}
	public LocalDate getDataEmissao() {
		return dataEmissao;
	}
	public void setDataEmissao(LocalDate dataEmissao) {
		this.dataEmissao = dataEmissao;
	}
	public LocalDate getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	public Entrega getEntrega() {
		return entrega;
	}
	public void setEntrega(Entrega entrega) {
		this.entrega = entrega;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	
}
