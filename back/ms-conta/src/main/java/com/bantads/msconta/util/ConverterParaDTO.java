package com.bantads.msconta.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bantads.msconta.dto.ClienteDTO;
import com.bantads.msconta.dto.ContaDTO;
import com.bantads.msconta.dto.GerenteDTO;
import com.bantads.msconta.dto.HistoricoMovimentacaoDTO;
import com.bantads.msconta.entity.cud.Cliente;
import com.bantads.msconta.entity.cud.Conta;
import com.bantads.msconta.entity.cud.Gerente;
import com.bantads.msconta.entity.r.ClienteR;
import com.bantads.msconta.entity.r.ContaR;
import com.bantads.msconta.entity.r.GerenteR;
import com.bantads.msconta.entity.r.HistoricoMovimentacaoR;
import com.bantads.msconta.service.ClienteService;
import com.bantads.msconta.service.GerenteService;

@Service
public class ConverterParaDTO {

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private GerenteService gerenteService;
    
    public ContaDTO converterContaRparaContaDTO(ContaR conta) {
    	
    	ContaDTO contaDTO = new ContaDTO();
	    contaDTO.setId(conta.getId());
	    contaDTO.setNumeroConta(conta.getNumeroConta());
	    contaDTO.setLimite(conta.getLimite());
	    contaDTO.setSaldo(conta.getSaldo());
	    contaDTO.setDataCriacao(conta.getDataCriacao());
	    contaDTO.setStatusConta(conta.getStatusConta());
	    
	    ClienteR cliente = new ClienteR();
	    cliente.setId(conta.getCliente().getId());
	    cliente.setCpf(conta.getCliente().getCpf());
	    
	    GerenteR gerente = new GerenteR();
	    gerente.setId(conta.getGerente().getId());
	    gerente.setCpf(conta.getGerente().getCpf());
	    gerente.setQntClientes(conta.getGerente().getQntClientes());
	    
	    return contaDTO;

    }
    
    public List<ContaDTO> converterListaContaRparaContaDTO(List<ContaR> contas) {
        return contas.stream()
                     .map(this::converterContaRparaContaDTO)
                     .collect(Collectors.toList());
    }

	public List<ContaDTO> converterListaContaParaListaContaDTO(List<Conta> contas) {
		
        List<Cliente> clientes = clienteService.findAll();
        List<Gerente> gerentes = gerenteService.findAll();
        
	    // Mapear clientes e gerentes por ID para fácil acesso
	    Map<Long, Cliente> clienteMap = clientes.stream()
	        .collect(Collectors.toMap(Cliente::getId, cliente -> cliente));
	    Map<Long, Gerente> gerenteMap = gerentes.stream()
	        .collect(Collectors.toMap(Gerente::getId, gerente -> gerente));

	    // Converter cada Conta para ContaDTO
	    List<ContaDTO> contaDTOs = contas.stream().map(conta -> {
	        ContaDTO contaDTO = new ContaDTO();
	        contaDTO.setId(conta.getId());
	        contaDTO.setNumeroConta(conta.getNumeroConta());
	        contaDTO.setLimite(conta.getLimite());
	        contaDTO.setSaldo(conta.getSaldo());
	        contaDTO.setDataCriacao(conta.getDataCriacao());
	        contaDTO.setStatusConta(conta.getStatusConta());

	        // Obter e definir o cliente
	        Cliente cliente = clienteMap.get(conta.getIdCliente());
	        if (cliente != null) {
	            ClienteDTO clienteDTO = new ClienteDTO();
	            clienteDTO.setId(cliente.getId());
	            clienteDTO.setNome(cliente.getNome());
	            clienteDTO.setCpf(cliente.getCpf());
	            contaDTO.setCliente(clienteDTO);
	        }

	        // Obter e definir o gerente
	        Gerente gerente = gerenteMap.get(conta.getIdGerente());
	        if (gerente != null) {
	            GerenteDTO gerenteDTO = new GerenteDTO();
	            gerenteDTO.setId(gerente.getId());
	            gerenteDTO.setNome(gerente.getNome());
	            gerenteDTO.setCpf(gerente.getCpf());
	            gerenteDTO.setQntClientes(gerente.getQntClientes());
	            contaDTO.setGerente(gerenteDTO);
	        }

	        return contaDTO;
	    }).collect(Collectors.toList());

	    return contaDTOs;
	}
	
	public ContaDTO converterContaParaContaDTO(Conta conta) {
	    
	    // Obter todos os clientes e gerentes
	    List<Cliente> clientes = clienteService.findAll();
	    List<Gerente> gerentes = gerenteService.findAll();
	    
	    // Mapear clientes e gerentes por ID para fácil acesso
	    Map<Long, Cliente> clienteMap = clientes.stream()
	        .collect(Collectors.toMap(Cliente::getId, cliente -> cliente));
	    Map<Long, Gerente> gerenteMap = gerentes.stream()
	        .collect(Collectors.toMap(Gerente::getId, gerente -> gerente));

	    ContaDTO contaDTO = new ContaDTO();
	    contaDTO.setId(conta.getId());
	    contaDTO.setNumeroConta(conta.getNumeroConta());
	    contaDTO.setLimite(conta.getLimite());
	    contaDTO.setSaldo(conta.getSaldo());
	    contaDTO.setDataCriacao(conta.getDataCriacao());
	    contaDTO.setStatusConta(conta.getStatusConta());

	    // Obter e definir o cliente
	    Cliente cliente = clienteMap.get(conta.getIdCliente());
	    if (cliente != null) {
	        ClienteDTO clienteDTO = new ClienteDTO();
	        clienteDTO.setId(cliente.getId());
	        clienteDTO.setNome(cliente.getNome());
	        clienteDTO.setCpf(cliente.getCpf());
	        contaDTO.setCliente(clienteDTO);
	    }

	    // Obter e definir o gerente
	    Gerente gerente = gerenteMap.get(conta.getIdGerente());
	    if (gerente != null) {
	        GerenteDTO gerenteDTO = new GerenteDTO();
	        gerenteDTO.setId(gerente.getId());
	        gerenteDTO.setNome(gerente.getNome());
	        gerenteDTO.setCpf(gerente.getCpf());
	        gerenteDTO.setQntClientes(gerente.getQntClientes());
	        contaDTO.setGerente(gerenteDTO);
	    }

	    return contaDTO;
	}

    public HistoricoMovimentacaoDTO converterParaHistoricoMovimentacaoDTO(HistoricoMovimentacaoR historico) {
        
        // Obter todos os clientes
        List<ClienteR> clientes = clienteService.findAllR();
        
        // Mapear clientes por ID para fácil acesso
        Map<Long, ClienteR> clienteMap = clientes.stream()
            .collect(Collectors.toMap(ClienteR::getId, cliente -> cliente));

        HistoricoMovimentacaoDTO historicoDTO = new HistoricoMovimentacaoDTO();
        historicoDTO.setTipo(historico.getTipoMovimentacao());
        historicoDTO.setValor(historico.getValor());
        historicoDTO.setDataHora(historico.getDataHora());

        // Obter e definir o cliente de origem
        ClienteR clienteOrigem = clienteMap.get(historico.getIdClienteOrigem());
        if (clienteOrigem != null) {
            ClienteDTO clienteOrigemDTO = new ClienteDTO();
            clienteOrigemDTO.setId(clienteOrigem.getId());
            clienteOrigemDTO.setNome(clienteOrigem.getNome());
            clienteOrigemDTO.setCpf(clienteOrigem.getCpf());
            historicoDTO.setClienteOrigem(clienteOrigemDTO);
        }

        // Obter e definir o cliente de destino
        ClienteR clienteDestino = clienteMap.get(historico.getIdClienteDestino());
        if (clienteDestino != null) {
            ClienteDTO clienteDestinoDTO = new ClienteDTO();
            clienteDestinoDTO.setId(clienteDestino.getId());
            clienteDestinoDTO.setNome(clienteDestino.getNome());
            clienteDestinoDTO.setCpf(clienteDestino.getCpf());
            historicoDTO.setClienteDestino(clienteDestinoDTO);
        }

        return historicoDTO;
    }

}
