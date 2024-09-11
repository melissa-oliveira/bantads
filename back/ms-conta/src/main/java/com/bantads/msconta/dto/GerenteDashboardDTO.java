package com.bantads.msconta.dto;

public class GerenteDashboardDTO {

	private Long id;
	private String nome;
	private Integer qntClientes;
	private Double saldoPositivo;
	private Double saldoNegativo;
	
	 public GerenteDashboardDTO(Long id, String nome, Integer qntClientes, Double saldoPositivo, Double saldoNegativo) {
        this.id = id;
        this.nome = nome;
        this.qntClientes = qntClientes;
        this.saldoPositivo = saldoPositivo;
        this.saldoNegativo = saldoNegativo;
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
	public Integer getQntClientes() {
		return qntClientes;
	}
	public void setQntClientes(Integer qntClientes) {
		this.qntClientes = qntClientes;
	}
	public Double getSaldoPositivo() {
		return saldoPositivo;
	}
	public void setSaldoPositivo(Double saldoPositivo) {
		this.saldoPositivo = saldoPositivo;
	}
	public Double getSaldoNegativo() {
		return saldoNegativo;
	}
	public void setSaldoNegativo(Double saldoNegativo) {
		this.saldoNegativo = saldoNegativo;
	}
}
