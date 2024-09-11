package com.bantads.saga.rabbitmq;

import java.io.Serializable;

import com.bantads.saga.dto.ContaDTO;

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
