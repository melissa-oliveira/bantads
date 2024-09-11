package com.bantads.msconta.entity.r;

import java.io.Serializable;

import com.bantads.msconta.constant.TipoMovimentacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name="historico_movimentacao")
public class HistoricoMovimentacaoR implements Serializable {
	private static final Long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="dataHora")
	private String dataHora;
	@Column(name="tipo_movimentacao")
	private TipoMovimentacao tipoMovimentacao;
	@Column(name="valor")
	private Double valor;
	@Column(name="id_cliente_origem")
	private Long idClienteOrigem;
	@Column(name="id_cliente_destino")
	private Long idClienteDestino;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDataHora() {
		return dataHora;
	}
	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}
	public TipoMovimentacao getTipoMovimentacao() {
		return tipoMovimentacao;
	}
	public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Long getIdClienteOrigem() {
		return idClienteOrigem;
	}
	public void setIdClienteOrigem(Long idClienteOrigem) {
		this.idClienteOrigem = idClienteOrigem;
	}
	public Long getIdClienteDestino() {
		return idClienteDestino;
	}
	public void setIdClienteDestino(Long idClienteDestino) {
		this.idClienteDestino = idClienteDestino;
	}
	public static Long getSerialversionuid() {
		return serialVersionUID;
	}
}


