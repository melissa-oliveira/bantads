package com.bantads.msgerente.rabbitmq;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

import com.bantads.msgerente.dto.GerenteDTO;
import com.bantads.msgerente.entity.Gerente;
import com.bantads.msgerente.repository.GerenteRepository;
import com.bantads.msgerente.service.GerenteService;

@RabbitListener(queues = "gerente")
public class GerenteConsumer {

	@Autowired
    private RabbitTemplate template;
	
	@Autowired
    private RabbitMQConfig config;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private GerenteRepository gerenteRepository;
    
    @Autowired
    private GerenteService gerenteService;
    
    @RabbitHandler
    public GerenteTransfer receive(@Payload GerenteTransfer gerenteTransfer) {
        System.out.println("MS-GERENTE LOG (GerenteConsumer): Mensagem recebida por ms-gerente: " + gerenteTransfer.getAction());

        // Manipular ação nula
        if (gerenteTransfer.getAction() == null) {
            System.out.println("MS-GERENTE LOG (GerenteConsumer): Mensagem nula recebida");
            gerenteTransfer = new GerenteTransfer();
            gerenteTransfer.setAction("gerente-failed");
            return gerenteTransfer;
        }

        switch (gerenteTransfer.getAction()) {
            case "gerente-register":
                return handleGerenteRegister(gerenteTransfer);
                
            case "gerente-edit":
                return handleGerenteEdit(gerenteTransfer);
          
            case "gerente-delete":
                return handleGerenteDelete(gerenteTransfer);
                
            default:
                System.out.println("MS-GERENTE LOG (GerenteConsumer): Acao nao reconhecida");
                gerenteTransfer.setAction("gerente-failed");
                return gerenteTransfer;
        }
    }
    
    private GerenteTransfer handleGerenteRegister(GerenteTransfer gerenteTransfer) {
        GerenteDTO gerente = gerenteTransfer.getGerente();

        if (gerente == null) {
            System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou - Gerente nulo");
            gerenteTransfer.setAction("gerente-failed");
            return gerenteTransfer;
        }

        if (gerente.getEmail() != null && 
        		gerente.getCpf() != null &&
        		gerente.getNome() != null &&
        		gerente.getTelefone() != null) {
        	
            // Manipular email já existente
            Gerente gerenteEntity = gerenteRepository.findByEmail(gerente.getEmail());
            if (gerenteEntity != null) {
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou - Email já cadastrado");
                gerenteTransfer.setAction("gerente-failed/email-registered");
                return gerenteTransfer;
            }
            
            // Manipular cpf já existente
            gerenteEntity = gerenteRepository.findByCpf(gerente.getCpf());
            if (gerenteEntity != null) {
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou - CPF já cadastrado");
                gerenteTransfer.setAction("gerente-failed/cpf-registered");
                return gerenteTransfer;
            }

            // Tentar cadastro de gerente
            try {
                gerenteRepository.save(modelMapper.map(gerente, Gerente.class));
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Gerente registrado com sucesso");
                gerenteTransfer.setAction("gerente-ok");
            } catch (Exception e) {
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou");
                e.printStackTrace();
                gerenteTransfer.setAction("gerente-failed");
            }
            return gerenteTransfer;
        }

        gerenteTransfer.setAction("gerente-auth-failed");
        System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou");
        return gerenteTransfer;
    }
    
    private GerenteTransfer handleGerenteEdit(GerenteTransfer gerenteTransfer) {
        GerenteDTO gerente = gerenteTransfer.getGerente();

        // Erro - Gerente não informado
        if (gerente == null) {
            System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou - Gerente nulo");
            gerenteTransfer.setAction("gerente-failed");
            return gerenteTransfer;
        }
        
        Optional<Gerente> gerenteSalvo = gerenteRepository.findById(gerente.getId());

        // Erro - Gerente não existe no banco de dados
        if (gerenteSalvo == null) {
            System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou - Gerente não existe");
            gerenteTransfer.setAction("gerente-failed/gerente-nonexistent");
            return gerenteTransfer;
        }

        if (gerente.getEmail() != null && 
        		gerente.getCpf() != null &&
        		gerente.getNome() != null &&
        		gerente.getTelefone() != null) {
        	
            // Manipular email já existente
            Gerente gerenteEntity = gerenteRepository.findByEmailExcetoEste(gerente.getEmail(), gerente.getId());
            if (gerenteEntity != null) {
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou - Email ja cadastrado");
                gerenteTransfer.setAction("gerente-failed/email-registered");
                return gerenteTransfer;
            }
            
            // Manipular cpf já existente
            gerenteEntity = gerenteRepository.findByCpfExcetoEste(gerente.getCpf(), gerente.getId());
            if (gerenteEntity != null) {
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Registo de gerente no ms-gerente falhou - CPF já cadastrado");
                gerenteTransfer.setAction("gerente-failed/cpf-registered");
                return gerenteTransfer;
            }

            // Tentar cadastro de gerente
            try {
                gerenteService.update(modelMapper.map(gerente, Gerente.class));
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Gerente editado com sucesso");
                gerenteTransfer.setAction("gerente-ok");
            } catch (Exception e) {
                System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Ediçcao de gerente no ms-gerente falhou");
                e.printStackTrace();
                gerenteTransfer.setAction("gerente-failed");
            }
            return gerenteTransfer;
        }

        gerenteTransfer.setAction("gerente-auth-failed");
        System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Edicao de gerente no ms-gerente falhou");
        return gerenteTransfer;
    }

    private GerenteTransfer handleGerenteDelete(GerenteTransfer gerenteTransfer) {
        GerenteDTO gerente = gerenteTransfer.getGerente();
        Gerente gerenteOptional = gerenteRepository.findByCpf(gerente.getCpf());

        if (gerenteOptional != null) {
            Gerente gerenteObj = gerenteOptional;
            gerenteRepository.delete(gerenteObj);
            
            gerenteTransfer.getGerente().setEmail(gerenteObj.getEmail());
            gerenteTransfer.getGerente().setNome(gerenteObj.getNome());
            gerenteTransfer.getGerente().setTelefone(gerenteObj.getTelefone());
            
            gerenteTransfer.setAction("gerente-deleted");
        } else {
            System.out.println("MS-GERENTE LOG (GerenteAuthConsumer): Gerente não encontrado para deletar");
            gerenteTransfer.setAction("gerente-failed");
        }
        return gerenteTransfer;
    }


}
