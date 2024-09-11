package com.bantads.msconta.entity.cud;

import java.io.Serializable;

import com.bantads.msconta.constant.StatusConta;

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
@Table (name="conta")
public class Conta implements Serializable{
	private static final Long serialVersionUID = 1L;
	
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
	@Column(name="id_cliente")
	private Long idCliente;
	@Column(name="id_gerente")
	private Long idGerente;
    @ManyToOne
    @JoinColumn(name = "gerente_id")
    private Gerente gerente;
		
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
	public Long getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}
	public Long getIdGerente() {
		return idGerente;
	}
	public void setIdGerente(Long idGerente) {
		this.idGerente = idGerente;
	}
	public static Long getSerialversionuid() {
		return serialVersionUID;
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
