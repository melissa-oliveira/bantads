package com.bantads.mscliente.entity;

import java.io.Serializable;

import com.bantads.mscliente.constant.StatusConta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name="cliente")
public class Cliente implements Serializable {
	private static final Long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="nome")
	private String nome;
	@Column(name="email")
	private String email;
	@Column(name="cpf")
	private String cpf;
	@Column(name="telefone")
	private String telefone;
	@Column(name="salario")
	private Double salario;
	@Column(name="id_endereco")
	private Long id_endereco;
	@Column(name="status_conta")
	private StatusConta status_conta;

	public Cliente(Long id, String nome, String email, String cpf, String telefone, double salario, long id_endereco ) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.cpf = cpf;
		this.telefone = telefone;
		this.salario = salario;
		this.id_endereco = id_endereco;
	}

	public Cliente() {
		super();
	}

	
	public static Long getSerialversionuid() {
		return serialVersionUID;
	}


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
	
	public double getSalario() {
		return salario;
	}


	public void setSalario(double salario) {
		this.salario = salario;
	}

	public long getIdEndereco() {
		return id_endereco;
	}


	public void setIdEndereco(long id_endereco) {
		this.id_endereco = id_endereco;
	}

	public StatusConta getStatusConta() {
		return status_conta;
	}

	public void setStatusConta(StatusConta status_conta) {
		this.status_conta = status_conta;
	}
		
}
