package com.bantads.mscliente.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bantads.mscliente.dto.ClienteDTO;
import com.bantads.mscliente.entity.Cliente;

@Configuration
public class RabbitMQConfig {

	@Bean
	public Queue clienteQueue() {
		System.out.println("MS-CLIENTE LOG (RabbitMQConfig): Criando fila cliente");
	    return new Queue("cliente");
	}
	
	@Bean
	public ClienteConsumer receiver() {
	    return new ClienteConsumer();
	}

	@Bean
	public DefaultClassMapper classMapper() {
		DefaultClassMapper classMapper = new DefaultClassMapper();
		Map<String, Class<?>> idClassMapping = new HashMap();
		classMapper.setTrustedPackages("*");
		idClassMapping.put("com.bantads.saga.rabbitmq.ClienteTransfer", ClienteTransfer.class);
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

        // Definindo o mapeamento específico
        modelMapper.addMappings(new PropertyMap<ClienteDTO, Cliente>() {
            @Override
            protected void configure() {
            	map().setId(source.getId());
                map().setNome(source.getNome());
                map().setEmail(source.getEmail());
                map().setCpf(source.getCpf());
                map().setTelefone(source.getTelefone());
                map().setSalario(source.getSalario());
                map().setStatusConta(source.getStatusConta());
                map().setIdEndereco(source.getEndereco().getId()); 
            }
        });

        return modelMapper;
    }
}
