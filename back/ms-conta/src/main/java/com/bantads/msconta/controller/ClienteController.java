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

import com.bantads.msconta.entity.cud.Cliente;
import com.bantads.msconta.entity.r.ClienteR;
import com.bantads.msconta.service.ClienteService;

@CrossOrigin
@RestController
public class ClienteController {

    @Autowired
    private ClienteService clienteService;
        
    @PostMapping("/conta/cliente")
    public Cliente inserir(@RequestBody Cliente cliente) {
        clienteService.create(cliente);
        return clienteService.findById(cliente.getId());        
    }
    
    @GetMapping("/conta/cliente")
     public List<Cliente> buscar() {
        return clienteService.findAll();       
    }
    
    @GetMapping("/conta/cliente/read")
    public List<ClienteR> buscarR() {
       return clienteService.findAllR();       
   }
    
    @GetMapping("/conta/cliente/{id}")
    public Cliente buscarPorId(@PathVariable("id") Long id) {      
        return clienteService.findById(id);      
    }
 
    @PutMapping("/conta/cliente")
    public Cliente atualizar(@RequestBody Cliente cliente) { 
        return clienteService.update(cliente);           
    }
    
    @DeleteMapping("/conta/cliente/{id}")
    public void deletarPorId(@PathVariable("id") Long id) {     
        clienteService.delete(id);       
    }
}
