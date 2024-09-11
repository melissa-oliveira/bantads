package com.bantads.msconta.entity.r;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="gerenteR")
public class GerenteR implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="nome")
	private String nome;
	@Column(name="cpf")
	private String cpf;
	@Column(name="qnt_clientes")
	private Integer qntClientes;
	@JsonManagedReference
    @OneToMany(mappedBy = "gerente", fetch = FetchType.EAGER)
    private List<ContaR> contas;
	
	
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
	public List<ContaR> getContas() {
		return contas;
	}
	public void setContas(List<ContaR> contas) {
		this.contas = contas;
	}
}