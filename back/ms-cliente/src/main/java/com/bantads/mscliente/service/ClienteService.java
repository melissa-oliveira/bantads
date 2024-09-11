package com.bantads.mscliente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.mscliente.dto.ClienteDTO;
import com.bantads.mscliente.entity.Cliente;
import com.bantads.mscliente.repository.ClienteRepository;
import com.bantads.mscliente.util.ConverterParaDTO;

@Service
@Transactional
public class ClienteService {
	@Autowired
	private ClienteRepository clienteRepository;
	
    @Autowired
    private ConverterParaDTO clienteConverter;
	
	public List<Cliente> findAll() {		
		return clienteRepository.findAll();
	}
	
    public ClienteDTO findClienteDTOByCPf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf);
        
        if (cliente == null) {
        	return new ClienteDTO();
        }
        return clienteConverter.convertToDTO(cliente);
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
	        existingCliente.setEmail(entity.getEmail());
	        existingCliente.setCpf(entity.getCpf());
	        existingCliente.setTelefone(entity.getTelefone());
	        existingCliente.setSalario(entity.getSalario());
	        existingCliente.setIdEndereco(entity.getIdEndereco());

	        Cliente ClienteAtualizado = clienteRepository.save(existingCliente);
	        return ClienteAtualizado;
	    } else {
	        return null;
	    }
	}

	public void delete(Long id) {
		clienteRepository.deleteById(id);
	}
	
	public Cliente findByNome(String nome) {
		return clienteRepository.findByNome(nome);
	}

}
