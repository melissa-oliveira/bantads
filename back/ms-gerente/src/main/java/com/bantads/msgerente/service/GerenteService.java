package com.bantads.msgerente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msgerente.entity.Gerente;
import com.bantads.msgerente.repository.GerenteRepository;
import com.bantads.msgerente.service.interfaces.IGerenteService;

@Service
@Transactional
public class GerenteService implements IGerenteService {

    @Autowired
    private GerenteRepository gerenteRepository;

    @Override
    public List<Gerente> findAll() {       
        return gerenteRepository.findAll();
    }

    @Override
    public Gerente findById(Long id) {     
        return gerenteRepository.findById(id).get();   
    }

    @Override
    public Gerente create(Gerente entity) {       
        return gerenteRepository.save(entity);
    }

    @Override
    public Gerente update(Gerente entity) {
        Gerente gerenteExistente = gerenteRepository.findById(entity.getId()).get();
        gerenteExistente.setCpf(entity.getCpf());
        gerenteExistente.setNome(entity.getNome());
        gerenteExistente.setEmail(entity.getEmail());
        gerenteExistente.setTelefone(entity.getTelefone());  
        Gerente gerenteAtualizado = gerenteRepository.save(gerenteExistente);
        
        return gerenteAtualizado;
    }

    @Override
    public void delete(Long id) {
        gerenteRepository.deleteById(id);
    }

    @Override
    public Gerente findByNome(String nome) {
        return gerenteRepository.findByNome(nome);
    }
    
    
    
}
   