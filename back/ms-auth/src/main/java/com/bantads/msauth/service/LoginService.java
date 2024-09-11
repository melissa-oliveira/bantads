package com.bantads.msauth.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msauth.dto.LoginDTO;
import com.bantads.msauth.entity.Login;
import com.bantads.msauth.repository.LoginRepository;
import com.bantads.msauth.util.ConverterParaDTO;
import com.bantads.msauth.util.CriptografiaSenha;

@Service
@Transactional
public class LoginService {
	@Autowired
	private LoginRepository loginRepository;
	
	@Autowired
	private ConverterParaDTO loginConverter;
	
	public List<Login> findAll() {		
		return loginRepository.findAll();
	}
	
	public Login findById(String id) {		
		return loginRepository.findById(id).orElse(null);	
	}
	
	public Login findByLogin(String email) {		
		return loginRepository.findByEmail(email);	
	}
	
	public LoginDTO findLoginDTOByCpf(String cpf) {		
		Login login = loginRepository.findByCpf(cpf);	
		return loginConverter.converterToLoginDTO(login);
	}

	public Login create(Login entity) {	
		
		String saltValue = CriptografiaSenha.getSaltvalue(10);
		String passwordEnc = CriptografiaSenha.generateSecurePassword(entity.getSenha(), saltValue);
		
		entity.setSalt(saltValue);
		entity.setSenha(passwordEnc);
		
		return loginRepository.save(entity);
	}
	
	public Login update(Login entity) {
        Login existingUsuario = loginRepository.findById(entity.getId()).orElse(null);
        if (existingUsuario != null) {

            if (entity.getSenha() != null && !entity.getSenha().isEmpty() && entity.getSenha() != "") {
                if (!Objects.equals(entity.getSenha(), existingUsuario.getSenha())) {
                    String saltValue = existingUsuario.getSalt();
                    String passwordEnc = CriptografiaSenha.generateSecurePassword(entity.getSenha(), saltValue);
                    existingUsuario.setSenha(passwordEnc);
                }
            }

            existingUsuario.setEmail(entity.getEmail());
            existingUsuario.setCpf(entity.getCpf());

            Login usuarioAtualizado = loginRepository.save(existingUsuario);
            return usuarioAtualizado;
        } else {
            return null;
        }
    }

	public void delete(String id) {
	    loginRepository.deleteById(id);
	}
	
	public Login login(Login usuario) {
		Login usuarioTmp = loginRepository.findByEmail(usuario.getEmail());
	    if (usuarioTmp != null &&
	    		CriptografiaSenha.verifyUserPassword(usuario.getSenha(), usuarioTmp.getSenha(), usuarioTmp.getSalt())) {
	        return usuarioTmp;
	    } else {
	        return null;
	    }
	}

}
