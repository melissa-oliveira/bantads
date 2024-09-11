package com.bantads.msconta.rabbitmq.cud;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Bean(name = "conta")
	public Queue contaQueue() {
		System.out.println("MS-CONTA LOG (RabbitMQConfig): Criando fila conta");
	    return new Queue("conta");
	}
	
	@Bean
	public ContaConsumer receiver() {
	    return new ContaConsumer();
	}
	
	@Bean
	public DefaultClassMapper classMapper() {
		DefaultClassMapper classMapper = new DefaultClassMapper();
		Map<String, Class<?>> idClassMapping = new HashMap();
		classMapper.setTrustedPackages("*");
		idClassMapping.put("com.bantads.saga.rabbitmq.ContaTransfer", ContaTransfer.class);
		classMapper.setIdClassMapping(idClassMapping);
	
		return classMapper;
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
	    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
	    converter.setClassMapper(classMapper());

	    return converter;
	}
	
	@Bean
	 public ModelMapper modelMapper() {
       ModelMapper modelMapper = new ModelMapper();
       return modelMapper;
	}
}
