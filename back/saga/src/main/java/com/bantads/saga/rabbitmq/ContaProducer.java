package com.bantads.saga.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bantads.saga.dto.ContaDTO;

public class ContaProducer {

	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	@Qualifier("conta")
	private Queue queue;
	
	public void send(ContaTransfer conta) {
	    System.out.println(conta);
	    this.template.convertAndSend(this.queue.getName(), conta);
	}

	public ContaTransfer sendAndReceive(ContaDTO conta, String action) {
	    ContaTransfer dt = new ContaTransfer(conta, action);

	    ContaTransfer resposta = (ContaTransfer) this.template.convertSendAndReceive(this.queue.getName(), dt);

	    return resposta;
	}
}
