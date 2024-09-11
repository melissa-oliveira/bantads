package com.bantads.msconta.rabbitmq.cud;

import java.io.Serializable;

import com.bantads.msconta.dto.ContaDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContaTransfer implements Serializable {

	ContaDTO conta;
	String action;
	
	public ContaTransfer() {}
	
	public ContaTransfer(ContaDTO conta, String action) {
	    this.conta = conta;
	    this.action = action;
	}

	public ContaDTO getConta() {
		return conta;
	}

	public void setConta(ContaDTO conta) {
		this.conta = conta;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
