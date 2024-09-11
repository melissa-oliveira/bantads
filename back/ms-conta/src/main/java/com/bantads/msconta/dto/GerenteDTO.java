package com.bantads.msconta.dto;

import java.io.Serializable;

public class GerenteDTO implements Serializable   {

	private Long id;
	private String nome;
	private String cpf;
	private Integer qntClientes;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public Integer getQntClientes() {
		return qntClientes;
	}
	public void setQntClientes(Integer qntClientes) {
		this.qntClientes = qntClientes;
	}
}
