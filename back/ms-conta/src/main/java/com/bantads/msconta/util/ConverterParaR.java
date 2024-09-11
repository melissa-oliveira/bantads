package com.bantads.msconta.util;

import org.springframework.stereotype.Service;

import com.bantads.msconta.dto.ClienteDTO;
import com.bantads.msconta.dto.ContaDTO;
import com.bantads.msconta.dto.GerenteDTO;
import com.bantads.msconta.entity.r.ClienteR;
import com.bantads.msconta.entity.r.GerenteR;
import com.bantads.msconta.entity.r.ContaR;

@Service
public class ConverterParaR {

	public ContaR converterParaContaR(ContaDTO contaDTO) {
	    ContaR contaR = new ContaR();

	    contaR.setDataCriacao(contaDTO.getDataCriacao());
	    contaR.setLimite(contaDTO.getLimite());
	    contaR.setNumeroConta(contaDTO.getNumeroConta());
	    contaR.setSaldo(contaDTO.getSaldo());

	    if (contaDTO.getCliente() != null) {
	        contaR.setCliente(converterParaClienteR(contaDTO.getCliente()));
	    } else {
	        contaR.setCliente(null);
	    }

	    contaR.setGerente(converterParaGerenteR(contaDTO.getGerente()));

	    return contaR;
	}

	public ClienteR converterParaClienteR(ClienteDTO clienteDTO) {
	    if (clienteDTO == null) {
	        return new ClienteR();
	    }

	    ClienteR clienteR = new ClienteR();
	    clienteR.setId(clienteDTO.getId());
	    clienteR.setCpf(clienteDTO.getCpf());
	    clienteR.setNome(clienteDTO.getNome());
	    return clienteR;
	}

	public GerenteR converterParaGerenteR(GerenteDTO gerenteDTO) {
	    if (gerenteDTO == null) {
	        return new GerenteR();
	    }
	    
	    GerenteR gerenteR = new GerenteR();
	    gerenteR.setId(gerenteDTO.getId());
	    gerenteR.setCpf(gerenteDTO.getCpf());
	    gerenteR.setNome(gerenteDTO.getNome());
	    gerenteR.setQntClientes(gerenteDTO.getQntClientes());
	    return gerenteR;
	}
}
