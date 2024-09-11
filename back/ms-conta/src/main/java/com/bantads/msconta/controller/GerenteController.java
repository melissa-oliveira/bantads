package com.bantads.msconta.controller;

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

import com.bantads.msconta.dto.GerenteDashboardDTO;
import com.bantads.msconta.entity.cud.Gerente;
import com.bantads.msconta.entity.r.GerenteR;
import com.bantads.msconta.service.GerenteService;

@CrossOrigin
@RestController
public class GerenteController {

    @Autowired
    private GerenteService gerenteService;
        
    @PostMapping("/conta/gerente")
    public Gerente inserir(@RequestBody Gerente gerente) {
        gerenteService.create(gerente);
        return gerenteService.findById(gerente.getId());        
    }
    
    @GetMapping("/conta/gerente")
    public List<Gerente> buscar() {
        return gerenteService.findAll();       
    }
    
    @GetMapping("/conta/gerente/read")
    public List<GerenteR> buscarR() {
        return gerenteService.findAllR();       
    }
    
    @GetMapping("/conta/gerente/dashboard")
    public List<GerenteDashboardDTO> buscarDashboard() {
        return gerenteService.findAllDashboard();       
    }
    
    @GetMapping("/conta/gerente/{id}")
    public Gerente buscarPorId(@PathVariable("id") Long id) {      
        return gerenteService.findById(id);      
    }
 
    @PutMapping("/conta/gerente")
    public Gerente atualizar(@RequestBody Gerente gerente) { 
        return gerenteService.update(gerente);           
    }
    
    @DeleteMapping("/conta/gerente/{id}")
    public void deletarPorId(@PathVariable("id") Long id) {     
        gerenteService.delete(id);       
    }
    
}
