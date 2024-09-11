package com.bantads.mscliente.rabbitmq;

import java.io.Serializable;

import com.bantads.mscliente.dto.ClienteDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteTransfer implements Serializable {
	ClienteDTO cliente;
	String action;

	public ClienteTransfer() {}

	public ClienteTransfer(ClienteDTO cliente, String action) {
	    this.cliente = cliente;
	    this.action = action;
  }

	public ClienteDTO getCliente() {
		return cliente;
	}

	public void setCliente(ClienteDTO cliente) {
		this.cliente = cliente;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
