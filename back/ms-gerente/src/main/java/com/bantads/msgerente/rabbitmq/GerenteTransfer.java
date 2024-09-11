package com.bantads.msgerente.rabbitmq;

import java.io.Serializable;

import com.bantads.msgerente.dto.GerenteDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GerenteTransfer implements Serializable {

	GerenteDTO gerente;
	String action;

	public GerenteTransfer() {}

	public GerenteTransfer(GerenteDTO gerente, String action) {
	    this.gerente = gerente;
	    this.action = action;
	}

	public GerenteDTO getGerente() {
	    return gerente;
	}

	public void setGerente(GerenteDTO gerente) {
	    this.gerente = gerente;
	}

	public String getAction() {
	    return action;
	}

	public void setAction(String action) {
	    this.action = action;
	}

}
