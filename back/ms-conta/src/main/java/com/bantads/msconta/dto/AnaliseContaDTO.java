package com.bantads.msconta.dto;

import com.bantads.msconta.constant.StatusConta;

public class AnaliseContaDTO {
	private String cpfCliente;
	private StatusConta resultado;
	private String motivoRecusa;
	
	
	public String getCpfCliente() {
		return cpfCliente;
	}
	public void setCpfCliente(String cpfCliente) {
		this.cpfCliente = cpfCliente;
	}
	public StatusConta getResultado() {
		return resultado;
	}
	public void setResultado(StatusConta resultado) {
		this.resultado = resultado;
	}
	public String getMotivoRecusa() {
		return motivoRecusa;
	}
	public void setMotivoRecusa(String motivoRecusa) {
		this.motivoRecusa = motivoRecusa;
	}
	
}
