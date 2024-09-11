package com.bantads.msconta.dto;

import java.io.Serializable;

import com.bantads.msconta.constant.StatusConta;

public class ContaDTO implements Serializable  {

	private Long id;
	private String numeroConta;
	private Double limite;
	private Double saldo;
	private String dataCriacao;
	private ClienteDTO cliente;
	private GerenteDTO gerente;
	private StatusConta statusConta;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNumeroConta() {
		return numeroConta;
	}
	public void setNumeroConta(String numeroConta) {
		this.numeroConta = numeroConta;
	}
	public Double getLimite() {
		return limite;
	}
	public void setLimite(Double limite) {
		this.limite = limite;
	}
	public ClienteDTO getCliente() {
		return cliente;
	}
	public void setCliente(ClienteDTO cliente) {
		this.cliente = cliente;
	}
	public GerenteDTO getGerente() {
		return gerente;
	}
	public void setGerente(GerenteDTO gerente) {
		this.gerente = gerente;
	}
	public Double getSaldo() {
		return saldo;
	}
	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}
	public String getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(String dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public StatusConta getStatusConta() {
		return statusConta;
	}
	public void setStatusConta(StatusConta statusConta) {
		this.statusConta = statusConta;
	}
}
