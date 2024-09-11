package com.bantads.saga.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bantads.saga.dto.ClienteDTO;

public class ClienteProducer {
	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	@Qualifier("cliente")
	private Queue queue;
	
	public void send(ClienteTransfer cliente) {
		System.out.println(cliente);
		this.template.convertAndSend(this.queue.getName(), cliente);
	}
	
	public ClienteTransfer sendAndReceive(ClienteDTO cliente, String action) {
		ClienteTransfer dt = new ClienteTransfer(cliente, action);
        
		ClienteTransfer resposta = (ClienteTransfer) this.template.convertSendAndReceive(this.queue.getName(), dt);
		
		return resposta;
	}
	
	public ClienteTransfer sendAndReceiveInt(Long cliente, String action) {
		ClienteDTO cli = new ClienteDTO();
		cli.setId(cliente);
		ClienteTransfer dt = new ClienteTransfer(cli, action);
		ClienteTransfer resposta = (ClienteTransfer) this.template.convertSendAndReceive(this.queue.getName(), dt);
		return resposta;
	}
}
