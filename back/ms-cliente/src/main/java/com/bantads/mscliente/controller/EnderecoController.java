package com.bantads.mscliente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.mscliente.entity.Endereco;
import com.bantads.mscliente.service.EnderecoService;

@CrossOrigin
@RestController
public class EnderecoController {
    @Autowired
    private EnderecoService enderecoService;
        
    @PostMapping("/cliente/endereco")
    public Endereco inserir(@RequestBody Endereco endereco) {
        enderecoService.create(endereco);
        return enderecoService.findById(endereco.getId());        
    }
    @GetMapping("/cliente/endereco")
     public List<Endereco> buscar() {
        return enderecoService.findAll();       
    }
    @GetMapping("/cliente/endereco/{id}")
    public Endereco buscarPorId(@PathVariable("id") Long id) {      
        return enderecoService.findById(id);      
    }
 
    @PutMapping("/cliente/endereco")
    public Endereco atualizar(@RequestBody Endereco endereco) { 
        return enderecoService.update(endereco);           
    }
    
    @DeleteMapping("/cliente/endereco/{id}")
    public void deletarPorId(@PathVariable("id") Long id) {     
        enderecoService.delete(id);       
    }
}

