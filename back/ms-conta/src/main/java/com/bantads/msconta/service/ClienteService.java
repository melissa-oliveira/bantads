package com.bantads.msconta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.entity.cud.Cliente;
import com.bantads.msconta.entity.r.ClienteR;
import com.bantads.msconta.repository.cud.ClienteRepository;
import com.bantads.msconta.repository.r.ClienteRRepository;

@Service
@Transactional
public class ClienteService {

	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ClienteRRepository clienteReadRepository;
	
	public List<Cliente> findAll() {		
		return clienteRepository.findAll();
	}
	
	public List<ClienteR> findAllR() {		
		return clienteReadRepository.findAll();
	}
	
	public Cliente findById(Long id) {		
		return clienteRepository.findById(id).orElse(null);	
	}

	public Cliente create(Cliente entity) {		
		return clienteRepository.save(entity);
	}
	
	public Cliente update(Cliente entity) {
	    Cliente existingCliente = clienteRepository.findById(entity.getId()).orElse(null);
	    if (existingCliente != null) {
	        existingCliente.setNome(entity.getNome());
	        existingCliente.setCpf(entity.getCpf());

	        Cliente ClienteAtualizado = clienteRepository.save(existingCliente);
	        return ClienteAtualizado;
	    } else {
	        return null;
	    }
	}

	public void delete(Long id) {
		clienteRepository.deleteById(id);
	}
	
	public Cliente findByCpf(String cpf) {
		return clienteRepository.findByCpf(cpf);
	}
}
