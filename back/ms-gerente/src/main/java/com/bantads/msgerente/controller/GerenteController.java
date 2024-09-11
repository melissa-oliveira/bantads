package com.bantads.msgerente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.msgerente.entity.Gerente;
import com.bantads.msgerente.service.GerenteService;

@CrossOrigin
@RestController
public class GerenteController {
    @Autowired
    private GerenteService gerenteService;
        
    @PostMapping("/gerente/gerente")
    public Gerente inserir(@RequestBody Gerente gerente) {
        gerenteService.create(gerente);
        return gerenteService.findByNome(gerente.getNome());        
    }
    @GetMapping("/gerente/gerente")
     public List<Gerente> buscar() {
        return gerenteService.findAll();       
    }
    @GetMapping("/gerente/gerente/{id}")
    public Gerente buscarPorId(@PathVariable("id") Long id) {      
        return gerenteService.findById(id);      
    }
 
    @PutMapping("/gerente/gerente")
    public Gerente atualizar(@RequestBody Gerente gerente) { 
        return gerenteService.update(gerente);           
    }
    
    @DeleteMapping("/gerente/gerente/{id}")
    public void deletarPorId(@PathVariable("id") Long id) {     
        gerenteService.delete(id);       
    }
}
