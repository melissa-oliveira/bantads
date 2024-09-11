package com.bantads.msauth.controller;

import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

import com.bantads.msauth.dto.LoginDTO;
import com.bantads.msauth.entity.Login;
import com.bantads.msauth.service.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
public class LoginController {
	
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
	private ObjectMapper objectMapper;
    
    @GetMapping("/auth/usuario/cpf/{cpf}")
    public LoginDTO findByCpf(@PathVariable("cpf") String cpf) {
        return loginService.findLoginDTOByCpf(cpf);
    }

    @PostMapping("/auth/usuario")
    public Login inserir(@RequestBody Login usuario){
        loginService.create(usuario);
        Login usuarioCriado = loginService.findByLogin(usuario.getEmail());
;        
        return usuarioCriado;
    }

    @GetMapping("/auth/usuario")
    public List<Login> findAll() {
        return loginService.findAll();
    }

    @GetMapping("/auth/usuario/{login}")
    public Login findByLogin(@PathVariable("login") String login) {
        return loginService.findByLogin(login);
    }

    @PutMapping("/auth/usuario")
    public Login update(@RequestBody Login usuario) {
        return loginService.update(usuario);
    }

    @DeleteMapping("/auth/usuario/{id}")
    public void delete(@PathVariable("id") String id) {
        loginService.delete(id);
    }
    
    @PostMapping("/auth/login")
    ResponseEntity<Login> login(@RequestBody Login usuario) {
    	Login usuarioTmp = loginService.login(usuario);
        if (usuarioTmp != null)
            return ResponseEntity.status(200).body(usuarioTmp);
        else
            return ResponseEntity.status(401).build();
    }
}