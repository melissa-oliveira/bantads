package com.bantads.msconta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.dto.GerenteDashboardDTO;
import com.bantads.msconta.entity.cud.Gerente;
import com.bantads.msconta.entity.r.GerenteR;
import com.bantads.msconta.repository.cud.GerenteRepository;
import com.bantads.msconta.repository.r.GerenteRRepository;

@Service
@Transactional
public class GerenteService {
	@Autowired
	GerenteRepository gerenteRepository;
	
	@Autowired
	GerenteRRepository gerenteReadRepository;
	
	
	public List<GerenteDashboardDTO> findAllDashboard() {
		return gerenteRepository.findGerenteDashboardData();
	}

	public List<Gerente> findAll() {		
		return gerenteRepository.findAll();
	}
	
	public List<GerenteR> findAllR() {		
		return gerenteReadRepository.findAll();
	}

	public Gerente findById(Long id) {		
		return gerenteRepository.findById(id).orElse(null);	
	}

	public Gerente create(Gerente entity) {		
		return gerenteRepository.save(entity);
	}

	public Gerente update(Gerente entity) {
	    Gerente existingGerente = gerenteRepository.findById(entity.getId()).orElse(null);
	    if (existingGerente != null) {
	        existingGerente.setNome(entity.getNome());
	        existingGerente.setCpf(entity.getCpf());
	        existingGerente.setQntClientes(entity.getQntClientes());

	        Gerente gerenteAtualizado = gerenteRepository.save(existingGerente);
	        return gerenteAtualizado;
	    } else {
	        return null;
	    }
	}

	public void delete(Long id) {
		gerenteRepository.deleteById(id);
	}

	public Gerente findByCpf(String cpf) {
		return gerenteRepository.findByCpf(cpf);
	}

}
