package com.bantads.msauth.rabbitmq;

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
	
	@Bean
	public Queue authQueue() {
		System.out.println("MS-AUTH LOG (RabbitMQConfig): Criando fila auth");
	    return new Queue("auth");
	}
	
	@Bean
	public AuthConsumer receiver() {
	    return new AuthConsumer();
	}

	@Bean
	public DefaultClassMapper classMapper() {
		DefaultClassMapper classMapper = new DefaultClassMapper();
		Map<String, Class<?>> idClassMapping = new HashMap();
		classMapper.setTrustedPackages("*");
		idClassMapping.put("com.bantads.saga.rabbitmq.AuthTransfer", AuthTransfer.class);
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
	public ModelMapper modelMapper(){
	    return new ModelMapper();
	}

}
	