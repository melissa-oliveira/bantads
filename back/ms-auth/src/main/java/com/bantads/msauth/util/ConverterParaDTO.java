package com.bantads.msauth.util;

import org.springframework.stereotype.Service;

import com.bantads.msauth.dto.LoginDTO;
import com.bantads.msauth.entity.Login;

@Service
public class ConverterParaDTO {
	
	public LoginDTO converterToLoginDTO(Login login) {
		
		LoginDTO dto = new LoginDTO();
		
		dto.setId(login.getId());
		dto.setEmail(login.getEmail());
		dto.setSenha(login.getSenha());
		dto.setCpf(login.getCpf());
		dto.setTipo(login.getTipo());
		
		return dto;
	}

}
