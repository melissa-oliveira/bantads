package com.bantads.saga.dto;

import java.io.Serializable;

import com.bantads.saga.constant.StatusConta;

public class ClienteDTO implements Serializable {
	private Long id;
	private String nome;
	private String email;
	private String senha;
	private String cpf;
	private String telefone;
	private Double salario;
	private EnderecoDTO endereco;
	private Long id_gerente;
	private Long id_conta;
	private StatusConta statusConta;
	
	
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public Double getSalario() {
		return salario;
	}
	public void setSalario(Double salario) {
		this.salario = salario;
	}
	public EnderecoDTO getEndereco() {
		return endereco;
	}
	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}
	public Long getId_gerente() {
		return id_gerente;
	}
	public void setId_gerente(Long id_gerente) {
		this.id_gerente = id_gerente;
	}
	public Long getId_conta() {
		return id_conta;
	}
	public void setId_conta(Long id_conta) {
		this.id_conta = id_conta;
	}
	public StatusConta getStatusConta() {
		return this.statusConta;
	}
	public void setStatusConta(StatusConta statusConta) {
		this.statusConta = statusConta;
	}
}
