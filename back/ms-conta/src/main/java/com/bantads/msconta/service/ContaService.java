package com.bantads.msconta.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.util.ConverterParaDTO;
import com.bantads.msconta.constant.StatusConta;
import com.bantads.msconta.constant.TipoMovimentacao;
import com.bantads.msconta.dto.TransacaoDTO;
import com.bantads.msconta.entity.cud.Cliente;
import com.bantads.msconta.entity.cud.Conta;
import com.bantads.msconta.entity.cud.Gerente;
import com.bantads.msconta.entity.r.ContaR;
import com.bantads.msconta.entity.r.GerenteR;
import com.bantads.msconta.entity.r.HistoricoMovimentacaoR;
import com.bantads.msconta.rabbitmq.r.ContaRProducer;
import com.bantads.msconta.repository.cud.ClienteRepository;
import com.bantads.msconta.repository.cud.ContaRepository;
import com.bantads.msconta.repository.cud.GerenteRepository;
import com.bantads.msconta.repository.r.ClienteRRepository;
import com.bantads.msconta.repository.r.ContaRRepository;
import com.bantads.msconta.repository.r.GerenteRRepository;
import com.bantads.msconta.repository.r.HistoricoMovimentacaoRRepository;

@Service
@Transactional
public class ContaService {

	@Autowired
	ContaRRepository contaReadRepository;
	
	@Autowired
	GerenteRRepository gerenteReadRepository;	
	
	@Autowired
	ClienteRRepository clienteReadRepository;
	
	@Autowired
	HistoricoMovimentacaoRRepository historicoReadRepository;
	
	@Autowired
	ContaRepository contaRepository;
	
	@Autowired
	GerenteRepository gerenteRepository;	
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ContaRProducer contaReadProducer;
	
	@Autowired
    private ConverterParaDTO converterDTO;
	
	// Banco de Leitura
	
	public List<Conta> findByStatusConta(StatusConta statusConta) {		
		return contaRepository.findByStatusConta(statusConta);
	}
	
	public List<Conta> findByStatusContaAndCpfGerente(StatusConta statusConta, String cpf) {	
		Gerente gerente = gerenteRepository.findByCpf(cpf);
		return contaRepository.findByStatusContaAndGerente(statusConta, gerente.getId());
	}
	
	public List<Conta> find3MelhoresContasGerente(String cpfGerente) {	
	    Gerente gerente = gerenteRepository.findByCpf(cpfGerente);
	    List<Conta> melhoresContas = contaRepository.findMelhoresContasDeGerente(gerente.getId(), StatusConta.APROVADO);
	    
	    if (melhoresContas.size() > 3) {
	        return melhoresContas.subList(0, 3);
	    } else {
	        return melhoresContas;
	    }
	}
	
	public TransacaoDTO transacao(TransacaoDTO transacao) {		

		HistoricoMovimentacaoR historico = new HistoricoMovimentacaoR();
		transacao.setSuccess(false);
		
		// Transferência
		if (transacao.getTipo() == TipoMovimentacao.TRANSFERENCIA) {			
			Conta contaOrigem = contaRepository.findByNumeroConta(transacao.getContaOrigem());
			Double saldoAnteriorOrigem = contaOrigem.getSaldo();
			
			Conta contaDestino = contaRepository.findByNumeroConta(transacao.getContaDestino());
			Double saldoAnteriorDestino = contaDestino.getSaldo();
			Double limite = contaOrigem.getLimite() == null ? 0 : contaOrigem.getLimite();
			
			if (saldoAnteriorOrigem - transacao.getValor() + limite >= 0) {
				contaOrigem.setSaldo(saldoAnteriorOrigem - transacao.getValor());
				contaDestino.setSaldo(saldoAnteriorDestino + transacao.getValor());
				
				contaRepository.save(contaOrigem);
				contaReadProducer.send(converterDTO.converterContaParaContaDTO(contaOrigem), "update-conta");
				
				contaRepository.save(contaDestino);
				contaReadProducer.send(converterDTO.converterContaParaContaDTO(contaDestino), "update-conta");
				
				historico.setIdClienteOrigem(contaOrigem.getIdCliente());
				historico.setIdClienteDestino(contaDestino.getIdCliente());
				
				transacao.setSuccess(true);
			}
			
		} 
		// Saque ou Depósito
		else {
			Conta conta = contaRepository.findByNumeroConta(transacao.getContaOrigem());
		 	Double saldoAnterior = conta.getSaldo();
		 
			if (transacao.getTipo() == TipoMovimentacao.SAQUE) {
				Double limite = conta.getLimite() == null ? 0 : conta.getLimite();
				if (saldoAnterior - transacao.getValor() + limite >= 0) {
					conta.setSaldo(saldoAnterior - transacao.getValor());					
					transacao.setSuccess(true);
				}
			} else if (transacao.getTipo() == TipoMovimentacao.DEPOSITO) {
				 conta.setSaldo(saldoAnterior + transacao.getValor());				 
				transacao.setSuccess(true);
			}
			
			contaRepository.save(conta);		
			contaReadProducer.send(converterDTO.converterContaParaContaDTO(conta), "update-conta");
			 
			historico.setIdClienteOrigem(conta.getIdCliente());
		}
		
		if (transacao.getSuccess() == true) {
			// Gravar histórico de movimentação
			String dataCriacao = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      	    historico.setDataHora(dataCriacao);
			historico.setValor(transacao.getValor());
			historico.setTipoMovimentacao(transacao.getTipo());
			historicoReadRepository.save(historico);
		}
		
		// Definir data e hora em transacao
		transacao.setDataHora(historico.getDataHora());
		
		return transacao;
	}
	
	public List<Conta> findAll() {		
		return contaRepository.findAll();
	}
	
	public List<ContaR> findAllR() {		
		return contaReadRepository.findAll();
	}
	
	// Banco de Comandos
    
	public Conta aprovarConta(String cpfCliente) {	
		Cliente cliente = clienteRepository.findByCpf(cpfCliente);
		
		// Editar conta
		Conta conta = contaRepository.findByIdCliente(cliente.getId());
		conta.setStatusConta(StatusConta.APROVADO);
		this.update(conta);
		contaReadProducer.send(converterDTO.converterContaParaContaDTO(conta), "update-conta");
		
		// Incrementar quantidade de clientes de gerente
		Optional<Gerente> gerente = gerenteRepository.findById(conta.getIdGerente());
		Integer qntAnteriorClientes = gerente.get().getQntClientes();
		gerente.get().setQntClientes(qntAnteriorClientes + 1);
		gerenteRepository.save(gerente.get());
		contaReadProducer.send(converterDTO.converterContaParaContaDTO(conta), "update-gerente");
		
		return conta;
	}
	
	public Conta recusarConta(String cpfCliente) {		
		Cliente cliente = clienteRepository.findByCpf(cpfCliente);
		Conta conta = contaRepository.findByIdCliente(cliente.getId());
		conta.setStatusConta(StatusConta.RECUSADO);
		contaRepository.delete(conta);
        contaReadProducer.send(converterDTO.converterContaParaContaDTO(conta), "delete-conta");
		
		return conta;
	}

	public Conta create(Conta entity) {		
		Conta conta = contaRepository.save(entity);
		contaReadProducer.send(converterDTO.converterContaParaContaDTO(conta), "create-conta");
		
		return conta;
	}

	public Conta update(Conta entity) {
	    Conta existingConta = contaRepository.findById(entity.getId()).orElse(null);
	    if (existingConta != null) {
	        existingConta.setNumeroConta(entity.getNumeroConta());
	        existingConta.setDataCriacao(entity.getDataCriacao());
	        existingConta.setLimite(entity.getLimite());
	        existingConta.setIdCliente(entity.getIdCliente());
	        existingConta.setIdGerente(entity.getIdGerente());

	        Conta contaAtualizada = contaRepository.save(existingConta);
	        contaReadProducer.send(converterDTO.converterContaParaContaDTO(contaAtualizada), "update-conta");
	        return contaAtualizada;
	    } else {
	        return null;
	    }
	}

	public void delete(Long id) {
		contaRepository.deleteById(id);
	}
	
	public Conta findById(Long id) {		
		return contaRepository.findById(id).orElse(null);	
	}
	
	public Conta findByIdCliente(Long id) {		
		return contaRepository.findByIdCliente(id);	
	}
	
	public Long getGerenteComMenorNumeroDeClientes() {
		List<Long> resultados = contaRepository.findGerenteComMenorNumeroDeClientes();
		if (resultados.isEmpty()) {
			return null; 
        }
        return resultados.get(0); 
	}
	
}
