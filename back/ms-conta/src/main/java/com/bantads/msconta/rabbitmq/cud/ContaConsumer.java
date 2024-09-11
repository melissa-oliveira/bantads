package com.bantads.msconta.rabbitmq.cud;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

import com.bantads.msconta.constant.StatusConta;
import com.bantads.msconta.dto.ClienteDTO;
import com.bantads.msconta.dto.ContaDTO;
import com.bantads.msconta.dto.GerenteDTO;
import com.bantads.msconta.entity.cud.Cliente;
import com.bantads.msconta.entity.cud.Conta;
import com.bantads.msconta.entity.cud.Gerente;
import com.bantads.msconta.rabbitmq.r.ContaRProducer;
import com.bantads.msconta.repository.cud.ClienteRepository;
import com.bantads.msconta.repository.cud.ContaRepository;
import com.bantads.msconta.repository.cud.GerenteRepository;
import com.bantads.msconta.service.ClienteService;
import com.bantads.msconta.service.ContaService;
import com.bantads.msconta.service.GerenteService;
import com.bantads.msconta.util.CriarNumeroConta;

@RabbitListener(queues = "conta")
public class ContaConsumer {

	@Autowired
    private RabbitTemplate template;
	
	@Autowired
    private RabbitMQConfig config;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private ContaRepository contaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private GerenteRepository gerenteRepository;
        
    @Autowired
    private ContaService contaService;
    
    @Autowired
    private GerenteService gerenteService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private CriarNumeroConta criarNumeroConta;
    
	@Autowired
	ContaRProducer contaReadProducer;
    
    @RabbitHandler
    public ContaTransfer receive(@Payload ContaTransfer contaTransfer) {
	    System.out.println("MS-CONTA LOG (ContaConsumer): Mensagem recebida por ms-conta: " + contaTransfer.getAction());
	
	    // Manipular ação nula
	    if (contaTransfer.getAction() == null) {
	    	 System.out.println("MS-CONTA LOG (ContaConsumer): Mensagem nula recebida");
	    	 contaTransfer = new ContaTransfer();
	    	 contaTransfer.setAction("conta-failed");
	         return contaTransfer;
	    }
	
	    switch (contaTransfer.getAction()) {
	        case "conta-register":
	            return handleClontaRegister(contaTransfer);
	
	        case "conta-delete":
	            return handleContaDelete(contaTransfer);
	            
	        case "conta-new-gerente":
	            return handleConteNewGerente(contaTransfer);
	            
	        case "conta-delete-gerente":
	            return handleContaDeleteGerente(contaTransfer);
	            
	        case "conta-edit-gerente":
	            return handleConteEditGerente(contaTransfer);
	            
	        case "conta-edit-cliente":
	            return handleConteEditCliente(contaTransfer);
	            
	        case "create-conta":
	        case "update-conta":
	        case "delete-conta":
	        case "create-gerente":
	        case "update-gerente":
	        case "updat e-cliente":
	        	 System.out.println("MS-CONTA LOG (ContaConsumer): Mensagem de CQRS na fila errada");
	            return contaTransfer;
	
	        default:
	            System.out.println("MS-CONTA LOG (ContaConsumer): Acao nao reconhecida");
	            contaTransfer.setAction("conta-failed");
	            return contaTransfer;
	    }
    }
    
    private ContaTransfer handleClontaRegister(ContaTransfer contaTransfer) {
    	ContaDTO conta = contaTransfer.getConta();

    	if (conta == null) {
    	    System.out.println("MS-CONTA LOG (ContaConsumer): Registo de conta no ms-conta falhou - Conta nula");
    	    contaTransfer.setAction("conta-failed");
    	    return contaTransfer;
    	}

    	ClienteDTO cliente = conta.getCliente();

    	if (conta.getLimite() != null || 
    	        conta.getCliente() != null) {
    		   	    
    	    // Manipular conta já existente
    	    Conta contaEntity = contaRepository.findByNumeroConta(conta.getNumeroConta());
    	    if (contaEntity != null) {
    	        System.out.println("MS-CONTA LOG (ContaConsumer): Registo de conta no ms-conta falhou - Numero de Conta já cadastrado");
    	        contaTransfer.setAction("conta-failed/numero-conta-registered");
    	        return contaTransfer;
    	    }
    	    
    	    // Manipular cliente com conta já aberta
    	    contaEntity = contaRepository.findByIdCliente(conta.getCliente().getId());
    	    if (contaEntity != null) {
    	        System.out.println("MS-CONTA LOG (ContaConsumer): Registo de conta no ms-conta falhou - Cliente já possuí conta");
    	        contaTransfer.setAction("conta-failed/cliente-registered");
    	        return contaTransfer;
    	    }
    	      	    
    	    System.out.println("MS-CONTA LOG (ContaConsumer): Dados da conta corretos, entrando em tentativa de registro.");
    	    modelMapper = new RabbitMQConfig().modelMapper();

    	    // Tentar cadastro de cliente
    	    try {
    	        Cliente clienteSalvo = clienteRepository.save(modelMapper.map(cliente, Cliente.class));
    	        System.out.println("MS-CONTA LOG (ContaConsumer): Cliente registrado com sucesso");
    	        conta.getCliente().setId(clienteSalvo.getId());
    	        conta.getCliente().setCpf(clienteSalvo.getCpf());
    	        conta.getCliente().setNome(clienteSalvo.getNome());
    	        contaReadProducer.send(conta, "create-cliente");
    	        
    	        // Tentar cadastro de gerente
    	        try {
    	        	
    	        	// Selecionar gerente
    	        	List<Long> resultados = contaRepository.findGerenteComMenorNumeroDeClientes();
    	        	Long idGerenteSelecionado = null;
    	        	
    	        	if (!resultados.isEmpty()) {
    	        		idGerenteSelecionado = resultados.get(0);
    	        	} else {
    	        		System.out.println("MS-CONTA LOG (ContaConsumer): Nao foi possivel selecionar um gerente");
    	        	}
    	        	    	        	
    	        	Optional<Gerente> gerenteSalvo = gerenteRepository.findById(idGerenteSelecionado);
    	        	if (gerenteSalvo.isPresent()) {
    	        		System.out.println("MS-CONTA LOG (ContaConsumer): Gerente selecionado com sucesso");
    	        	}    	        	
        	        
        	        GerenteDTO gerenteSelecionado = new GerenteDTO();
        	        gerenteSelecionado.setId(gerenteSalvo.get().getId());
        	        gerenteSelecionado.setNome(gerenteSalvo.get().getNome());
        	        gerenteSelecionado.setCpf(gerenteSalvo.get().getCpf());
        	        
             	    conta.setGerente(gerenteSelecionado); 
             	    conta.setNumeroConta(criarNumeroConta.criarNumeroContaBaseadoDataHora());
             	    conta.setSaldo(0.0);
             	    conta.setLimite(conta.getLimite());
             	    String dataCriacao = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
                           .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
             	    conta.setDataCriacao(dataCriacao);
        	        
        	        // Tentar cadastro de conta
        	        try {
        	            Conta contaSalva = contaRepository.save(modelMapper.map(conta, Conta.class));    	            
        	            contaReadProducer.send(conta, "create-conta");
        	            System.out.println("MS-CONTA LOG (ContaConsumer): Conta registrada com sucesso");
        	            contaTransfer.setAction("conta-ok");
        	            return contaTransfer;
        	        } catch (Exception e) {
        	            System.out.println("MS-CONTA LOG (ContaConsumer): Registo de conta no ms-conta falhou");
        	            e.printStackTrace();
        	            contaTransfer.setAction("conta-failed");
        	            
        	            // Deletando cliente previamente cadastrado
        	            clienteRepository.deleteById(cliente.getId());
        	            System.out.println("MS-CONTA LOG (ContaConsumer): Cliente deletado");
        	            return contaTransfer;
        	        }
        	        
    	        } catch (Exception e) {
        	        System.out.println("MS-CONTA LOG (ContaConsumer): Registo de gerente da conta no ms-conta falhou");
        	        clienteRepository.deleteById(cliente.getId());
    	    	    System.out.println("MS-CONTA LOG (ContaConsumer): Cliente deletado com sucesso");
        	        e.printStackTrace();
        	        contaTransfer.setAction("conta-failed/gerente-failed");
        	        return contaTransfer;
        	    }
    	           	        
    	    } catch (Exception e) {
    	        System.out.println("MS-CONTA LOG (ContaConsumer): Registo de cliente da conta no ms-conta falhou");
    	        clienteRepository.deleteById(cliente.getId());
	    	    System.out.println("MS-CONTA LOG (ContaConsumer): Cliente deletado com sucesso");
    	        e.printStackTrace();
    	        contaTransfer.setAction("conta-failed/cliente-failed");
    	        return contaTransfer;
    	    }
    	}

    	contaTransfer.setAction("conta-failed");
    	System.out.println("MS-CONTA LOG (ContaConsumer): Registo de conta no ms-conta falhou");
    	return contaTransfer;

    }
    
    private ContaTransfer handleContaDelete(ContaTransfer contaTransfer) {
    	ContaDTO conta = contaTransfer.getConta();
    	Optional<Conta> contaOptional = contaRepository.findById(conta.getId());

    	if (contaOptional.isPresent()) {
    	    Conta contaObj = contaOptional.get();
    	    contaRepository.delete(contaObj);
    	    contaReadProducer.send(conta, "delete-conta");
    	    System.out.println("MS-CONTA LOG (ContaConsumer): Conta deletada com sucesso");
    	    contaTransfer.setAction("conta-deleted");
    	} else {
    	    System.out.println("MS-CONTA LOG (ContaConsumer): Conta nao encontrada para deletar");
    	    contaTransfer.setAction("conta-failed");
    	}
    	return contaTransfer;
    }
    
    private ContaTransfer handleConteNewGerente(ContaTransfer contaTransfer) {
    	   	
    	try {	 
        	ContaDTO conta = contaTransfer.getConta();
        	GerenteDTO gerente = conta.getGerente();
        	
	    	// Criar gerente
	    	Gerente gerenteEntity = new Gerente();
	    	gerenteEntity.setNome(gerente.getNome());
	    	gerenteEntity.setCpf(gerente.getCpf());
	    	
	    	// Selecionar gerente com maior número de clientes
	    	List<Long> idsGerenteSelecionado = contaRepository.findGerenteComMaiorNumeroDeClientes();
	    	Long idGerenteSelecionado = null;
	    	
			// Selecionar contas de gerente com maior número de clientes
	    	 if (!idsGerenteSelecionado.isEmpty()) {
	 			idGerenteSelecionado = idsGerenteSelecionado.get(0);
	 			Optional<Gerente> gerenteSelecionado = gerenteRepository.findById(idGerenteSelecionado);
	 			
	 			List<Long> idsContas = contaRepository.findContasDeGerente(idGerenteSelecionado);
	 			
	 			// Atribuir novo gerente a conta
	 			if ((!idsContas.isEmpty()) && idsContas.size() > 1) { 
	 				
	 				// Diminuir quantidade gerente anterior
	 				Integer qntClientesAnterior = gerenteSelecionado.get().getQntClientes();
	 				gerenteSelecionado.get().setQntClientes(qntClientesAnterior - 1);
	 				gerenteRepository.save(gerenteSelecionado.get());
 				
 					// Salvar gerente
 					gerenteEntity.setQntClientes(1);
 			    	gerenteEntity = gerenteRepository.save(gerenteEntity);
 			    	
 			    	GerenteDTO gerenteDTOSalvo = new GerenteDTO();
 			    	gerenteDTOSalvo.setCpf(gerenteEntity.getCpf());
 			    	gerenteDTOSalvo.setNome(gerenteEntity.getNome());
 			    	gerenteDTOSalvo.setQntClientes(gerenteEntity.getQntClientes());
 			    	conta.setGerente(gerenteDTOSalvo);
 			    	contaReadProducer.send(conta, "create-gerente");
 				
 					// Atribuir conta a gerente
 					Optional<Conta> contaSelecionada = contaRepository.findById(idsContas.get(0));
 					contaSelecionada.get().setIdGerente(gerenteEntity.getId());
 					contaRepository.save(contaSelecionada.get());
 					 					
 					contaTransfer.setAction("conta-ok");
 					return contaTransfer;
	 			} else {
	 				gerenteEntity.setQntClientes(0);
	 		    	gerenteEntity = gerenteRepository.save(gerenteEntity);
	 		    	conta.getGerente().setQntClientes(gerenteEntity.getQntClientes());
	 		    	
	 		    	GerenteDTO gerenteDTOSalvo = new GerenteDTO();
 			    	gerenteDTOSalvo.setCpf(gerenteEntity.getCpf());
 			    	gerenteDTOSalvo.setNome(gerenteEntity.getNome());
 			    	gerenteDTOSalvo.setQntClientes(gerenteEntity.getQntClientes());
 			    	conta.setGerente(gerenteDTOSalvo);
 			    	contaReadProducer.send(conta, "create-gerente");
	 		    	
	 				System.out.println("MS-GERENTE LOG (GerenteConsumer): Gerente com maior numero de clientes possui 0 ou 1 cliente");
	 				contaTransfer.setConta(null);
	 				contaTransfer.setAction("conta-impossible");
	 				return contaTransfer;
	 			}    		
	         } else {
	        	 
	        	gerenteEntity.setQntClientes(0);
	 		   	gerenteEntity = gerenteRepository.save(gerenteEntity);
	 		    conta.getGerente().setQntClientes(gerenteEntity.getQntClientes());
	 		    contaReadProducer.send(conta, "create-gerente");
	        	 
	     		System.out.println("MS-GERENTE LOG (GerenteConsumer): Nao existem gerentes cadastrados");
	     		contaTransfer.setConta(null);
	     		contaTransfer.setAction("conta-impossible");
	     		return contaTransfer;
	         }
    	 
    	 } catch (Exception e) {
    	 
	    	System.out.println("MS-GERENTE LOG (GerenteConsumer): Nao foi possivel selecionar atribuir o gerente a uma conta");
	  		contaTransfer.setConta(null);
	  		contaTransfer.setAction("conta-failed");
	    	    	
	    	return contaTransfer;
    	}
    }
    
    private ContaTransfer handleContaDeleteGerente(ContaTransfer contaTransfer) {   	
    	
    	try {    		
    		ContaDTO conta = contaTransfer.getConta();
    		String cpf = conta.getGerente().getCpf();
    		Gerente gerente = gerenteRepository.findByCpf(cpf);

        	// Validar se não é o último gerente
        	List<Gerente> todosGerentes = gerenteRepository.findAll();
        	
        	if(todosGerentes.size() > 1) {
        		
        		// Selecionar gerente com menor número de clientes
            	List<Long> resultados = contaRepository.findGerenteComMenorNumeroDeClientesExcetoCpf(cpf);
            	Long idGerenteSelecionado = null;
            	
            	if (!resultados.isEmpty()) {
            		idGerenteSelecionado = resultados.get(0);
            	} else {
            		System.out.println("MS-CONTA LOG (ContaConsumer): Nao foi possivel selecionar um gerente");
            		contaTransfer.setConta(null);
        	  		contaTransfer.setAction("conta-failed/select-failed");
        	  		return contaTransfer;
            	}
            	
            	// Encontrar contas de gerente a ser removido
     			List<Long> idsContas = contaRepository.findByStatusContaAndGerenteId(StatusConta.APROVADO, gerente.getId());
     			
     			if (!idsContas.isEmpty()) { 
     				
     				// Atribuir novo gerente as contas     				
     				Optional<Conta> contaTmp = null;
     				for (Integer i = 0; i < idsContas.size(); i++) {
     					contaTmp = contaRepository.findById(idsContas.get(i));
     					contaTmp.get().setIdGerente(idGerenteSelecionado);
     					contaRepository.save(contaTmp.get());
     				}
     				
     				// Salvar nova quantidade de clientes
     				Optional <Gerente> gerenteSelecionado = gerenteRepository.findById(idGerenteSelecionado);
     				Integer qntClientesAnterior = gerenteSelecionado.get().getQntClientes();
	 				gerenteSelecionado.get().setQntClientes(qntClientesAnterior + idsContas.size());
	 				gerenteRepository.save(gerenteSelecionado.get());
     				
     				// Remover gerente
     				gerenteRepository.deleteById(gerente.getId());
     				conta.getGerente().setId(gerente.getId());
     				conta.getGerente().setCpf(gerente.getCpf());
     				contaReadProducer.send(conta, "delete-gerente");
     				
            		contaTransfer.setConta(null);
        	  		contaTransfer.setAction("conta-ok/gerente-deleted");
        	  		return contaTransfer;
     			} else {
     				gerenteRepository.deleteById(gerente.getId());
     				conta.getGerente().setId(gerente.getId());
     				contaReadProducer.send(conta, "delete-gerente");
     				contaTransfer.setConta(null);
        	  		contaTransfer.setAction("conta-ok/gerente-deleted");
        	  		return contaTransfer;
     			}
        	} 
        	// Erro - Último gerente não pode ser removido
        	else {
        		System.out.println("MS-CONTA LOG (ContaConsumer): O ultimo gerente nao pode ser removido");
        		contaTransfer.setConta(null);
    	  		contaTransfer.setAction("conta-failed/last-gerente");
    	  		return contaTransfer;
        	}
        	       	
    	} catch (Exception e) {

	    	System.out.println("MS-CONTA LOG (GerenteConsumer): Nao foi possivel selecionar remover o gerente");
	  		contaTransfer.setConta(null);
	  		contaTransfer.setAction("conta-failed");
	    	    	
	    	return contaTransfer;
    	}
    }
    
    private ContaTransfer handleConteEditGerente(ContaTransfer contaTransfer) {
	   	
    	try {	 
        	ContaDTO conta = contaTransfer.getConta();
        	GerenteDTO gerente = conta.getGerente();
            	
			if (gerente == null) {
			    System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de gerente no ms-conta falhou - Gerente nulo");
			    contaTransfer.setAction("conta-failed");
			    return contaTransfer;
			}
 
        	Gerente gerenteSalvo = gerenteRepository.findByCpf(gerente.getCpf());
        	
			if (gerenteSalvo == null) {
			    System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de gerente no ms-conta falhou - Gerente não existe");
			    contaTransfer.setAction("conta-failed/gerente-nonexistent");
			    return contaTransfer;
			}
			
			if (gerente.getCpf() != null && 
	        		gerente.getNome() != null) {
				
				// Tentar cadastro de gerente
	            try {
	            	gerente.setId(gerenteSalvo.getId());
	            	gerente.setQntClientes(gerenteSalvo.getQntClientes());
	                gerenteSalvo = gerenteService.update(modelMapper.map(gerente, Gerente.class));
	                conta.getGerente().setId(gerenteSalvo.getId());
	                conta.getGerente().setCpf(gerenteSalvo.getCpf());
	                conta.getGerente().setNome(gerenteSalvo.getNome());
	                conta.getGerente().setQntClientes(gerenteSalvo.getQntClientes());
	                contaReadProducer.send(conta, "update-gerente");
	                System.out.println("MS-CONTA LOG (ContaConsumer): Gerente editado com sucesso");
	                contaTransfer.setAction("conta-ok");
	            } catch (Exception e) {
	                System.out.println("MS-CONTA LOG (ContaConsumer): Ediçcao de gerente no ms-conta falhou");
	                e.printStackTrace();
	                contaTransfer.setAction("conta-failed");
	            }
	            return contaTransfer;
	            
			} else {
				contaTransfer.setAction("conta-failed");
		        System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de gerente no ms-conta falhou");
		        return contaTransfer;
			}
	 		 
    	 
    	 } catch (Exception e) {
    	 
	    	System.out.println("MS-CONTA LOG (ContaConsumer): Nao foi possivel editar o gerente");
	  		contaTransfer.setConta(null);
	  		contaTransfer.setAction("conta-failed");
	    	    	
	    	return contaTransfer;
    	}
    }
    
    private ContaTransfer handleConteEditCliente(ContaTransfer contaTransfer) {
	   	
    	try {	 
        	ContaDTO conta = contaTransfer.getConta();
        	ClienteDTO cliente = conta.getCliente();
            	
			if (cliente == null) {
			    System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de cliente no ms-conta falhou - Cliente nulo");
			    contaTransfer.setAction("conta-failed");
			    return contaTransfer;
			}
 
        	System.out.println("MS-CONTA LOG (ContaConsumer): CPF DO CLIENTE - " + cliente.getCpf());
        	Cliente clienteSalvo = clienteRepository.findByCpf(cliente.getCpf());
        	
			if (clienteSalvo.getId() == null) {
			    System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de cliente no ms-conta falhou - Cliente nao existe");
			    contaTransfer.setAction("conta-failed/cliente-nonexistent");
			    return contaTransfer;
			}
			
        	Conta contaSalva = contaRepository.findByIdCliente(clienteSalvo.getId());
			
			if (contaSalva == null) {
			    System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de cliente no ms-conta falhou - Conta nao existe");
			    contaTransfer.setAction("conta-failed/cliente-nonexistent");
			    return contaTransfer;
			}
			
			if (cliente.getCpf() != null && 
					cliente.getNome() != null) {
				
				// Tentar cadastro de gerente
	            try {
	            	contaSalva.setLimite(conta.getLimite());
	            	contaSalva = contaService.update(contaSalva);
	            	conta.setNumeroConta(contaSalva.getNumeroConta());
	            	contaReadProducer.send(conta, "update-conta");
	            	
	            	cliente.setId(clienteSalvo.getId());
	                clienteSalvo = clienteService.update(modelMapper.map(cliente, Cliente.class));
	                conta.getCliente().setId(clienteSalvo.getId());
	                contaReadProducer.send(conta, "update-cliente");
	                System.out.println("MS-CONTA LOG (ContaConsumer): Cliente editado com sucesso");
	                contaTransfer.setAction("conta-ok");
	            } catch (Exception e) {
	                System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de cliente no ms-conta falhou");
	                e.printStackTrace();
	                contaTransfer.setAction("conta-failed");
	            }
	            return contaTransfer;
	            
			} else {
				contaTransfer.setAction("conta-failed");
		        System.out.println("MS-CONTA LOG (ContaConsumer): Edicao de cliente no ms-conta falhou");
		        return contaTransfer;
			}
	 		 
    	 
    	 } catch (Exception e) {
    	 
	    	System.out.println("MS-CONTA LOG (ContaConsumer): Nao foi possivel editar o cliente");
	  		contaTransfer.setConta(null);
	  		contaTransfer.setAction("conta-failed");
	    	    	
	    	return contaTransfer;
    	}
    }
}
