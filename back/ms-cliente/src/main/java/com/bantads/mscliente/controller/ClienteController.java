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

import com.bantads.mscliente.dto.ClienteDTO;
import com.bantads.mscliente.entity.Cliente;
import com.bantads.mscliente.service.ClienteService;

@CrossOrigin
@RestController
public class ClienteController {
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping("/cliente/cliente/cpf/{cpf}")
    public ClienteDTO buscarPorCpf(@PathVariable("cpf") String cpf) {      
        return clienteService.findClienteDTOByCPf(cpf);      
    }
        
    @PostMapping("/cliente/cliente")
    public Cliente inserir(@RequestBody Cliente cliente) {
        clienteService.create(cliente);
        return clienteService.findById(cliente.getId());        
    }
    @GetMapping("/cliente/cliente")
     public List<Cliente> buscar() {
        return clienteService.findAll();       
    }
    @GetMapping("/cliente/cliente/{id}")
    public Cliente buscarPorId(@PathVariable("id") Long id) {      
        return clienteService.findById(id);      
    }
 
    @PutMapping("/cliente/cliente")
    public Cliente atualizar(@RequestBody Cliente cliente) { 
        return clienteService.update(cliente);           
    }
    
    @DeleteMapping("/cliente/cliente/{id}")
    public void deletarPorId(@PathVariable("id") Long id) {     
        clienteService.delete(id);       
    }
}