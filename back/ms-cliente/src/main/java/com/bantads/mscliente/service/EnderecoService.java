package com.bantads.mscliente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.mscliente.entity.Endereco;
import com.bantads.mscliente.repository.EnderecoRepository;

@Service
@Transactional
public class EnderecoService {
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	public List<Endereco> findAll() {		
		return enderecoRepository.findAll();
	}
	
	public Endereco findById(Long id) {		
		return enderecoRepository.findById(id).orElse(null);	
	}

	public Endereco create(Endereco entity) {		
		return enderecoRepository.save(entity);
	}
	
	public Endereco update(Endereco entity) {
	    Endereco existingEndereco = enderecoRepository.findById(entity.getId()).orElse(null);
	    if (existingEndereco != null) {
	        existingEndereco.setTipo(entity.getTipo());
	        existingEndereco.setCep(entity.getCep());
	        existingEndereco.setLogradouro(entity.getLogradouro());
	        existingEndereco.setNumero(entity.getNumero());
	        existingEndereco.setCidade(entity.getCidade());
	        existingEndereco.setBairro(entity.getBairro());
	        existingEndereco.setEstado(entity.getEstado());
	        existingEndereco.setComplemento(entity.getComplemento());
	        enderecoRepository.save(existingEndereco);

	        Endereco EnderecoAtualizado = enderecoRepository.save(existingEndereco);
	        return EnderecoAtualizado;
	    } else {
	        return null;
	    }
	}

	public void delete(Long id) {
		enderecoRepository.deleteById(id);
	}
}
