package com.bantads.msconta.rabbitmq.r;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bantads.msconta.dto.ContaDTO;
import com.bantads.msconta.rabbitmq.cud.ContaTransfer;

import org.springframework.amqp.core.Queue;

public class ContaRProducer {
	
	@Autowired
    private RabbitTemplate template;

    @Autowired
    @Qualifier("cqrs")
    private Queue cqrsQueue;

    public void send(ContaDTO account, String action) {
        ContaTransfer dt = new ContaTransfer(account, action);
        System.out.println("Queue name: " + this.cqrsQueue.getName());
        this.template.convertAndSend(this.cqrsQueue.getName(), dt);
    }
}
