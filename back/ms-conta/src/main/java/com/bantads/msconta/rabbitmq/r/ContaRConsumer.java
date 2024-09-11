package com.bantads.msconta.rabbitmq.r;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;

import com.bantads.msconta.constant.StatusConta;
import com.bantads.msconta.entity.r.ClienteR;
import com.bantads.msconta.entity.r.ContaR;
import com.bantads.msconta.entity.r.GerenteR;
import com.bantads.msconta.rabbitmq.cud.ContaTransfer;
import com.bantads.msconta.repository.r.ClienteRRepository;
import com.bantads.msconta.repository.r.ContaRRepository;
import com.bantads.msconta.repository.r.GerenteRRepository;
import com.bantads.msconta.service.ContaService;
import com.bantads.msconta.util.ConverterParaR;

@RabbitListener(queues = "cqrs")
public class ContaRConsumer {
	
	@Autowired
    private RabbitTemplate template;
	
	@Autowired
    private RabbitMQRConfig config;

    @Autowired
    private ContaRRepository contaReadRepository;

    @Autowired
    private ClienteRRepository clienteReadRepository;

    @Autowired
    private GerenteRRepository gerenteReadRepository;
    
    @Autowired
    private ConverterParaR converter;

    @Autowired
    private ContaService service;

    @RabbitHandler
    public void receive(@Payload ContaTransfer dt) {
        System.out.println("MS-CONTA (ContaRConsumer): Acao recebida - " + dt.getAction());
        
        switch (dt.getAction()) {
        
        	// Manipular Conta
	        case "create-conta":
	        	System.out.println("MS-CONTA (ContaRConsumer): CPF GERENTE: " + dt.getConta().getGerente().getCpf());
	        	GerenteR gerenteConta = gerenteReadRepository.findByCpf(dt.getConta().getGerente().getCpf());
	        	System.out.println("Gerente encontrado: " + (gerenteConta != null ? gerenteConta.getId() : "Nenhum"));
	        	ClienteR clienteConta = clienteReadRepository.findByCpf(dt.getConta().getCliente().getCpf());
	        	ContaR novaConta = converter.converterParaContaR(dt.getConta());
	        	novaConta.setCliente(clienteConta);
	        	novaConta.setGerente(gerenteConta);
	        	novaConta.setStatusConta(StatusConta.ANALISE);
	        	novaConta = contaReadRepository.save(novaConta);
	        	System.out.println("MS-CONTA (ContaRConsumer): Operacao create-conta concluida: " + novaConta.getId());
	            break;
            case "update-conta":
            	ContaR contaParaEditar = contaReadRepository.findByNumeroConta(dt.getConta().getNumeroConta());
            	contaParaEditar.setLimite(dt.getConta().getLimite());
            	contaParaEditar.setSaldo(dt.getConta().getSaldo());
            	contaParaEditar.setStatusConta(dt.getConta().getStatusConta());
                ContaR contaEditada = contaReadRepository.save(contaParaEditar);
                System.out.println("MS-CONTA (ContaRConsumer): Operacao update-conta concluida: " + contaEditada.getId());
                break;
            case "delete-conta":
            	ContaR contaParaDeletar = contaReadRepository.findByNumeroConta(dt.getConta().getNumeroConta());
            	contaReadRepository.delete(contaParaDeletar);
                System.out.println("MS-CONTA (ContaRConsumer):Operacao delete-conta concluida: " + dt.getConta().getId());
                break;
                
            // Manipular Gerente
            case "create-gerente":
            	GerenteR novoGerente = gerenteReadRepository.save(converter.converterParaGerenteR(dt.getConta().getGerente()));
                System.out.println("MS-CONTA (ContaRConsumer): Operacao create-gerente concluida: " + novoGerente.getId());
                break;
            case "update-gerente":
            	GerenteR gerenteParaEditar = gerenteReadRepository.findByCpf(dt.getConta().getGerente().getCpf());
            	gerenteParaEditar.setNome(dt.getConta().getGerente().getNome());
            	gerenteParaEditar.setQntClientes(dt.getConta().getGerente().getQntClientes());
            	GerenteR gerenteEditado = gerenteReadRepository.save(gerenteParaEditar);
                System.out.println("MS-CONTA (ContaRConsumer): Operacao update-gerente concluida: " + gerenteEditado.getId());
                break;
            case "delete-gerente":
            	GerenteR gerenteParaDeletar = gerenteReadRepository.findByCpf(dt.getConta().getGerente().getCpf());
            	gerenteReadRepository.delete(gerenteParaDeletar);
                System.out.println("MS-CONTA (ContaRConsumer): Operacao update-gerente concluida");
                break;
                
            // Manipular Cliente   
            case "create-cliente":
            	ClienteR novoCliente = clienteReadRepository.save(converter.converterParaClienteR(dt.getConta().getCliente()));
                System.out.println("MS-CONTA (ContaRConsumer): Operacao create-cliente concluida: " + novoCliente.getId());
                break;
            case "update-cliente":
            	ClienteR clienteParaEditar = clienteReadRepository.findByCpf(dt.getConta().getCliente().getCpf());
            	clienteParaEditar.setNome(dt.getConta().getCliente().getNome());
            	ClienteR clienteSalvo = clienteReadRepository.save(clienteParaEditar);
                System.out.println("MS-CONTA (ContaRConsumer): Operacao update-cliente concluida: " + clienteSalvo.getId());
                break;                
        }
    }
}
