package com.bantads.msauth.rabbitmq;

import java.io.Serializable;

import com.bantads.msauth.dto.LoginDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthTransfer implements Serializable {
	LoginDTO login;
	String action;

	public AuthTransfer() {}

	public AuthTransfer(LoginDTO login, String action) {
	    this.login = login;
	    this.action = action;
  }

	public LoginDTO getLogin() {
		return login;
	}

	public void setLogin(LoginDTO login) {
		this.login = login;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
