package com.bantads.saga.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bantads.saga.dto.GerenteDTO;

public class GerenteProducer {
	
	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	@Qualifier("gerente")
	private Queue queue;

	public void send(GerenteTransfer gerente) {
	    System.out.println(gerente);
	    this.template.convertAndSend(this.queue.getName(), gerente);
	}

	public GerenteTransfer sendAndReceive(GerenteDTO gerente, String action) {
	    GerenteTransfer dt = new GerenteTransfer(gerente, action);

	    GerenteTransfer resposta = (GerenteTransfer) this.template.convertSendAndReceive(this.queue.getName(), dt);

	    return resposta;
	}

}
