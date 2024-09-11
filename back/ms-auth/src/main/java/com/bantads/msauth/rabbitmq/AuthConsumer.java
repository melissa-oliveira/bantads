package com.bantads.msauth.rabbitmq;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

import com.bantads.msauth.constant.TipoUsuario;
import com.bantads.msauth.dto.LoginDTO;
import com.bantads.msauth.entity.Login;
import com.bantads.msauth.repository.LoginRepository;
import com.bantads.msauth.service.LoginService;
import com.bantads.msauth.util.CriptografiaSenha;

import java.util.Optional;

@RabbitListener(queues = "auth")
public class AuthConsumer {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private LoginRepository loginRepository;
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private RabbitMQConfig config;

    @Autowired
    private ModelMapper modelMapper;
    

    @RabbitHandler
    public AuthTransfer receive(@Payload AuthTransfer authTransfer) {
        System.out.println("MS-AUTH LOG (AuthConsumer): Mensagem recebida por ms-auth: " + authTransfer.getAction());

        // Manipular ação nula
        if (authTransfer.getAction() == null) {
        	 System.out.println("MS-AUTH LOG (AuthConsumer): Mensagem nula recebida");
             authTransfer = new AuthTransfer();
             authTransfer.setAction("auth-failed");
             return authTransfer;
        }

        switch (authTransfer.getAction()) {
            case "auth-register":
                return handleAuthRegister(authTransfer);
                
            case "auth-edit":
                return handleAuthEdit(authTransfer);

            case "auth-delete":
                return handleAuthDelete(authTransfer);

            default:
                System.out.println("MS-AUTH LOG (AuthConsumer): Acao nao reconhecida");
                authTransfer.setAction("auth-failed");
                return authTransfer;
        }
    }

    private AuthTransfer handleAuthRegister(AuthTransfer authTransfer) {
        LoginDTO login = authTransfer.getLogin();

        if (login == null) {
            System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuário no ms-auth falhou - Login nulo");
            authTransfer.setAction("auth-failed");
            return authTransfer;
        }

        if (login.getEmail() != null && login.getSenha() != null) {
            // Manipular login já existente
            Login loginEntity = loginRepository.findByEmail(login.getEmail());
            if (loginEntity != null) {
                System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuario no ms-auth falhou - Email ja cadastrado");
                authTransfer.setAction("auth-failed/email-registered");
                return authTransfer;
            }

            // Define o tipo de usuário como cliente
            if (login.getTipo() == null) {
                login.setTipo(TipoUsuario.CLIENTE);
            }

            // Tentar cadastro de cliente
            try {
            	loginService.create(modelMapper.map(login, Login.class));
                System.out.println("MS-AUTH LOG (AuthConsumer): Usuario registrado com sucesso");
                authTransfer.setAction("auth-ok");
            } catch (Exception e) {
                System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuário no ms-auth falhou");
                e.printStackTrace();
                authTransfer.setAction("auth-failed");
            }
            return authTransfer;
        }

        authTransfer.setAction("auth-failed");
        System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuario no ms-auth falhou");
        return authTransfer;
    }
    
    private AuthTransfer handleAuthEdit(AuthTransfer authTransfer) {
        LoginDTO login = authTransfer.getLogin();

        if (login == null) {
            System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuario no ms-auth falhou - Login nulo");
            authTransfer.setAction("auth-failed");
            return authTransfer;
        }
        
        Login loginSalvo = loginRepository.findByCpf(login.getCpf());
        
        if (loginSalvo == null) {
            System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuario no ms-auth falhou - Usuario nao existe");
            authTransfer.setAction("auth-failed/user-nonexistent");
            return authTransfer;
        }
        
        if (login.getEmail() != null && 
        		login.getSenha() != null && 
        		login.getCpf() != null) {
        	
            // Manipular email já existente
            Login loginEntity = loginRepository.findByEmailAndIdNot(login.getEmail(), loginSalvo.getId());
            if (loginEntity != null) {
                System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuario no ms-auth falhou - Email ja cadastrado");
                authTransfer.setAction("auth-failed/email-registered");
                return authTransfer;
            }
            
            // Manipular CPF já existente
            loginEntity = loginRepository.findByCpfAndIdNot(login.getEmail(), loginSalvo.getId());
            if (loginEntity != null) {
                System.out.println("MS-AUTH LOG (AuthConsumer): Registo de usuario no ms-auth falhou - CPF ja cadastrado");
                authTransfer.setAction("auth-failed/cpf-registered");
                return authTransfer;
            }

            // Tentar edição de cliente
            try {
                login.setId(loginSalvo.getId());
                loginService.update(modelMapper.map(login, Login.class));
                System.out.println("MS-AUTH LOG (AuthConsumer): Usuario editado com sucesso");
                authTransfer.setAction("auth-ok");
            } catch (Exception e) {
                System.out.println("MS-AUTH LOG (AuthConsumer): Edicao de usuário no ms-auth falhou");
                e.printStackTrace();
                authTransfer.setAction("auth-failed");
            }
            return authTransfer;
        }

        authTransfer.setAction("auth-failed");
        System.out.println("MS-AUTH LOG (AuthConsumer): Edicao de usuario no ms-auth falhou");
        return authTransfer;
    }

    private AuthTransfer handleAuthDelete(AuthTransfer authTransfer) {
        LoginDTO login = authTransfer.getLogin();
        Login loginOptional = loginRepository.findByEmail(login.getEmail());
        
    	System.out.println("MS-AUTH LOG (AuthConsumer): email - " + login.getEmail());

        if (loginOptional != null) {
            Login loginObj = loginOptional;
            loginRepository.delete(loginObj);
            System.out.println("MS-AUTH LOG (AuthConsumer): Usuario deletado com sucesso");
            authTransfer.setAction("auth-deleted");
        } else {
            System.out.println("MS-AUTH LOG (AuthConsumer): Usuario nao encontrado para deletar");
            authTransfer.setAction("auth-failed");
        }
        return authTransfer;
    }
}
