package com.bantads.mscliente.rabbitmq;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

import com.bantads.mscliente.constant.StatusConta;
import com.bantads.mscliente.dto.ClienteDTO;
import com.bantads.mscliente.dto.EnderecoDTO;
import com.bantads.mscliente.entity.Cliente;
import com.bantads.mscliente.entity.Endereco;
import com.bantads.mscliente.repository.ClienteRepository;
import com.bantads.mscliente.repository.EnderecoRepository;
import com.bantads.mscliente.service.ClienteService;
import com.bantads.mscliente.service.EnderecoService;

@RabbitListener(queues = "cliente")
public class ClienteConsumer {
	
	@Autowired
    private RabbitTemplate template;

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private EnderecoRepository enderecoRepository;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EnderecoService enderecoService;
    
    @Autowired
    private RabbitMQConfig config;

    @Autowired
    private ModelMapper modelMapper;
    
    @RabbitHandler
    public ClienteTransfer receive(@Payload ClienteTransfer clienteTransfer) {
	    System.out.println("MS-CLIENTE LOG (ClienteConsumer): Mensagem recebida por ms-cliente: " + clienteTransfer.getAction());
	
	    // Manipular ação nula
	    if (clienteTransfer.getAction() == null) {
	    	 System.out.println("MS-CLIENTE LOG (ClienteConsumer): Mensagem nula recebida");
	    	 clienteTransfer = new ClienteTransfer();
	    	 clienteTransfer.setAction("cliente-failed");
	         return clienteTransfer;
	    }
	
	    switch (clienteTransfer.getAction()) {
	        case "cliente-register":
	            return handleClienteRegister(clienteTransfer);
	            
	        case "cliente-edit":
	            return handleClienteEdit(clienteTransfer);
	
	        case "cliente-delete":
	            return handleClienteDelete(clienteTransfer);
	
	        default:
	            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Acao nao reconhecida");
	            clienteTransfer.setAction("cliente-failed");
	            return clienteTransfer;
	    }
    }
    
    private ClienteTransfer handleClienteRegister(ClienteTransfer clienteTransfer) {
    	
        ClienteDTO cliente = clienteTransfer.getCliente();
       
        if (cliente == null) {
            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou - Cliente nulo");
            clienteTransfer.setAction("cliente-failed");
            return clienteTransfer;
        }
        
        EnderecoDTO endereco = cliente.getEndereco();

        if (cliente.getNome() != null || 
				cliente.getCpf() != null || 
				cliente.getEndereco() != null) {
        	
            // Manipular cliente já existente
            Cliente clienteEntity = clienteRepository.findByCpf(cliente.getCpf());
            if (clienteEntity != null) {
                System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou - CPF já cadastrado");
                clienteTransfer.setAction("cliente-failed/cpf-registered");
                return clienteTransfer;
            }
            
    		// Define o status da conta como ANALISE
            if (cliente.getStatusConta() == null) {
            	cliente.setStatusConta(StatusConta.ANALISE);
            }
            
        	System.out.println("MS-CLIENTE LOG (ClienteConsumer): Dados do cliente corretos, entrando em tentativa de registro.");
        	modelMapper = new RabbitMQConfig().modelMapper();

            // Tentar cadastro de endereço
        	try {            	        	
                Endereco enderecoSalvo = enderecoRepository.save(modelMapper.map(endereco, Endereco.class));
                System.out.println("MS-CLIENTE LOG (ClienteConsumer): Endereco registrado com sucesso");
                cliente.getEndereco().setId(enderecoSalvo.getId());
                
                // Tentar cadastro de cliente
                try {            	        	
                    clienteRepository.save(modelMapper.map(cliente, Cliente.class));
                    System.out.println("MS-CLIENTE LOG (ClienteConsumer): Cliente registrado com sucesso");
                    clienteTransfer.setAction("cliente-ok");
                } catch (Exception e) {
                    System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou");
                    e.printStackTrace();
                    clienteTransfer.setAction("cliente-failed");
                    
                    // Deletando endereço previamente cadastrado
                    enderecoRepository.deleteById(endereco.getId());
                    System.out.println("MS-CLIENTE LOG (ClienteConsumer): Endereco deletado");
                }
                
            } catch (Exception e) {
                System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de endereco do cliente no ms-cliente falhou");
                e.printStackTrace();
                clienteTransfer.setAction("cliente-failed/endereco-failed");
            }
        
            return clienteTransfer;
        }

        clienteTransfer.setAction("cliente-failed");
        System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou");
        return clienteTransfer;
    }

    private ClienteTransfer handleClienteDelete(ClienteTransfer clienteTransfer) {
        ClienteDTO cliente = clienteTransfer.getCliente();
        
        // Deletando cliente
        Cliente clienteSalvo = clienteRepository.findByCpf(cliente.getCpf());
        if (clienteSalvo != null) {
            clienteRepository.delete(clienteSalvo);
            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Cliente deletado com sucesso");
            clienteTransfer.setAction("cliente-deleted");
        } else {
            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Cliente nao encontrado para deletar");
            clienteTransfer.setAction("cliente-failed");
        }
        

        // Deletando endereço
        Optional<Endereco> enderecoSalvo = enderecoRepository.findById(clienteSalvo.getIdEndereco());
        if (enderecoSalvo.isPresent()) {
            enderecoRepository.deleteById(enderecoSalvo.get().getId());
            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Endereco de cliente deletado com sucesso");
            clienteTransfer.setAction("cliente-deleted");
        } else {
            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Endereco de cliente nao encontrado para deletar");
            clienteTransfer.setAction("cliente-failed/endereco-failed");
        }
        
        return clienteTransfer;
    }
    
    private ClienteTransfer handleClienteEdit(ClienteTransfer clienteTransfer) {
    	
        ClienteDTO cliente = clienteTransfer.getCliente();    
       
        if (cliente == null) {
            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou - Cliente nulo");
            clienteTransfer.setAction("cliente-failed");
            return clienteTransfer;
        }        
        
        Cliente clienteSalvo = clienteRepository.findByCpf(cliente.getCpf());
        
        if (clienteSalvo == null) {
            System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou - Cliente nao existe");
            clienteTransfer.setAction("cliente-failed");
            return clienteTransfer;
        }        
        
        EnderecoDTO endereco = cliente.getEndereco();
        Optional <Endereco> enderecoSalvo = enderecoRepository.findById(clienteSalvo.getIdEndereco());

        if (cliente.getNome() != null || 
				cliente.getCpf() != null || 
				cliente.getEndereco() != null) {
        	
            // Manipular CPF já existente
            Cliente clienteEntity = clienteRepository.findByCpfExcetoEste(cliente.getCpf(), clienteSalvo.getId());
            if (clienteEntity != null) {
                System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou - CPF já cadastrado");
                clienteTransfer.setAction("cliente-failed/cpf-registered");
                return clienteTransfer;
            }
            
            // Manipular Email já existente
            clienteEntity = clienteRepository.findByEmailExcetoEste(cliente.getEmail(), clienteSalvo.getId());
            if (clienteEntity != null) {
                System.out.println("MS-CLIENTE LOG (ClienteConsumer): Registo de cliente no ms-cliente falhou - Email já cadastrado");
                clienteTransfer.setAction("cliente-failed/email-registered");
                return clienteTransfer;
            }
                      
        	modelMapper = new RabbitMQConfig().modelMapper();

            // Tentar edicao de endereço
        	try {         
        		endereco.setId(enderecoSalvo.get().getId());
        		enderecoService.update(modelMapper.map(endereco, Endereco.class));
                System.out.println("MS-CLIENTE LOG (ClienteConsumer): Endereco editado com sucesso");
                cliente.getEndereco().setId(enderecoSalvo.get().getId());
                
                // Tentar edicao de cliente
                try {     
                	cliente.setId(clienteSalvo.getId());
                	cliente.setStatusConta(clienteSalvo.getStatusConta());
                    clienteService.update(modelMapper.map(cliente, Cliente.class));
                    System.out.println("MS-CLIENTE LOG (ClienteConsumer): Cliente editado com sucesso");
                    clienteTransfer.setAction("cliente-ok");
                } catch (Exception e) {
                    System.out.println("MS-CLIENTE LOG (ClienteConsumer): Edicao de cliente no ms-cliente falhou");
                    e.printStackTrace();
                    clienteTransfer.setAction("cliente-failed");                    
                }
                
            } catch (Exception e) {
                System.out.println("MS-CLIENTE LOG (ClienteConsumer): Edicao de endereco do cliente no ms-cliente falhou");
                e.printStackTrace();
                clienteTransfer.setAction("cliente-failed/endereco-failed");
            }
        
            return clienteTransfer;
        }

        clienteTransfer.setAction("cliente-failed");
        System.out.println("MS-CLIENTE LOG (ClienteConsumer): Edicao de cliente no ms-cliente falhou");
        return clienteTransfer;
    }

}
