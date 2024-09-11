package com.bantads.msconta.rabbitmq.r;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bantads.msconta.rabbitmq.cud.ContaTransfer;

@Configuration
public class RabbitMQRConfig {

	@Bean(name = "cqrs")
	public Queue contaRQueue() {
		System.out.println("MS-CONTA LOG (RabbitMQRConfig): Criando fila cqrs");
	    return new Queue("cqrs");
	}
	
	@Bean
	public ContaRConsumer receiverR() {
	    return new ContaRConsumer();
	}
	
	@Bean
	public ContaRProducer producerR() {
	    return new ContaRProducer();
	}
	
	@Bean
	public DefaultClassMapper classMapperR() {
		DefaultClassMapper classMapper = new DefaultClassMapper();
		Map<String, Class<?>> idClassMapping = new HashMap();
		classMapper.setTrustedPackages("*");
		idClassMapping.put("com.bantads.msconta.rabbitmq.cudContaTransfer", ContaTransfer.class);
		classMapper.setIdClassMapping(idClassMapping);
	
		return classMapper;
	}
}
