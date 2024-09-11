package com.bantads.msconta.dto;

import com.bantads.msconta.constant.TipoMovimentacao;
import com.bantads.msconta.entity.cud.Cliente;

public class HistoricoMovimentacaoDTO {

    private TipoMovimentacao tipo; 
    private Double valor;
    private ClienteDTO ClienteOrigem; 
    private ClienteDTO ClienteDestino;
    private String dataHora;
    
    
	public TipoMovimentacao getTipo() {
		return tipo;
	}
	public void setTipo(TipoMovimentacao tipo) {
		this.tipo = tipo;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public ClienteDTO getClienteOrigem() {
		return ClienteOrigem;
	}
	public void setClienteOrigem(ClienteDTO clienteOrigem) {
		ClienteOrigem = clienteOrigem;
	}
	public ClienteDTO getClienteDestino() {
		return ClienteDestino;
	}
	public void setClienteDestino(ClienteDTO clienteDestino) {
		ClienteDestino = clienteDestino;
	}
	public String getDataHora() {
		return dataHora;
	}
	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}
    
    
}
