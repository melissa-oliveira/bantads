package com.bantads.saga.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bantads.saga.dto.LoginDTO;
import org.springframework.amqp.core.Queue;

@Service
public class AuthProducer {
	
	@Autowired
    private RabbitTemplate template;

    @Autowired
    @Qualifier("auth")
    private Queue queue;

    public void send(AuthTransfer auth) {
        this.template.convertAndSend(this.queue.getName(), auth);
    }

    public AuthTransfer sendAndReceive(LoginDTO auth, String action) {
        AuthTransfer dt = new AuthTransfer(auth, action);
        AuthTransfer resposta = (AuthTransfer) this.template.convertSendAndReceive(this.queue.getName(), dt);

        return resposta;
    }
}
