package com.bantads.saga.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class GeneralConfig {

	@Bean
    public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");   
		
	    props.put("mail.host", "smtp.gmail.com");  
	    props.put("mail.port", "587");  
	    props.put("mail.username", "bantads.dac.tads@gmail.com");  
	    props.put("mail.password", "uvprcnhcwhagfmrb");  
	    props.put("mail.from.email", "bantads.dac.tads@gmail.com");
	    
	    props.put("mail.smtp.auth", "true");   
	    props.put("mail.smtp.starttls.enable", "true");

        javaMailSender.setJavaMailProperties(props);

        return javaMailSender;
    }
}
