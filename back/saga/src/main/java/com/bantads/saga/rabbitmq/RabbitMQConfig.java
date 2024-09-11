package com.bantads.saga.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	
	  @Bean
	  @Qualifier("cliente")
	  public Queue clienteQueue() {
	    System.out.println("MS-SAGA LOG (RabbitMQConfig): Criando fila cliente");
	    return new Queue("cliente");
	  }
	  
	  @Bean
	  @Qualifier("auth")
	  public Queue authQueue() {
	    System.out.println("MS-SAGA LOG (RabbitMQConfig): Criando fila auth");
	    return new Queue("auth");
	  }
	  
	  @Bean
	  @Qualifier("gerente")
	  public Queue gerenteQueue() {
	    System.out.println("MS-SAGA LOG (RabbitMQConfig): Criando fila gerente");
	    return new Queue("gerente");
	  }
	  
	  @Bean
	  @Qualifier("conta")
	  public Queue contaQueue() {
	    System.out.println("MS-SAGA LOG (RabbitMQConfig): Criando fila conta");
	    return new Queue("conta");
	  }
	  
	  @Bean
	  public ClienteProducer clienteSender() {
	    return new ClienteProducer();
	  }
	  
	  @Bean
	  public AuthProducer authSender() {
	    return new AuthProducer();
	  }
	  
	  @Bean
	  public GerenteProducer gerenteSender() {
	    return new GerenteProducer();
	  }
	  
	  @Bean
	  public ContaProducer contaSender() {
	    return new ContaProducer();
	  }
	  
	  @Bean
	  public DefaultClassMapper classMapper() {
	    DefaultClassMapper classMapper = new DefaultClassMapper();
	    Map<String, Class<?>> idClassMapping = new HashMap();
	    classMapper.setTrustedPackages("*");
	    idClassMapping.put("com.bantads.mscliente.rabbitmq.ClienteTransfer", ClienteTransfer.class);
	    idClassMapping.put("com.bantads.msauth.rabbitmq.AuthTransfer", AuthTransfer.class);
	    idClassMapping.put("com.bantads.msgerente.rabbitmq.GerenteTransfer", GerenteTransfer.class);
	    idClassMapping.put("com.bantads.msconta.rabbitmq.cud.ContaTransfer", ContaTransfer.class);
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
