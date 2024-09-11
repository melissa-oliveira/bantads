package com.bantads.msconta.entity.r;

import java.io.Serializable;

import com.bantads.msconta.constant.StatusConta;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="contaR")
public class ContaR implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name = "numero_conta", unique = true)
	private String numeroConta;
	@Column(name="data_criacao")
	private String dataCriacao;
	@Column(name="limite")
	private Double limite;
	@Column(name="saldo")
	private Double saldo;
	@Enumerated(EnumType.STRING)
	@Column(name="status_conta")
	private StatusConta statusConta;
	@JsonBackReference
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="id_cliente")
	private ClienteR cliente;
	@JsonBackReference
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="id_gerente")
	private GerenteR gerente;
    
		
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
	public String getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(String dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public Double getLimite() {
		return limite;
	}
	public void setLimite(Double limite) {
		this.limite = limite;
	}
	public ClienteR getCliente() {
		return cliente;
	}
	public void setCliente(ClienteR cliente) {
		this.cliente = cliente;
	}
	public GerenteR getGerente() {
		return gerente;
	}
	public void setGerente(GerenteR idGerente) {
		this.gerente = gerente;
	}
	public Double getSaldo() {
		return saldo;
	}
	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}
	public StatusConta getStatusConta() {
		return statusConta;
	}
	public void setStatusConta(StatusConta statusConta) {
		this.statusConta = statusConta;
	}
}
