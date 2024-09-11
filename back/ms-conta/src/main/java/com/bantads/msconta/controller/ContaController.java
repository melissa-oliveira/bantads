package com.bantads.msconta.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.msconta.constant.StatusConta;
import com.bantads.msconta.dto.AnaliseContaDTO;
import com.bantads.msconta.dto.ClienteDTO;
import com.bantads.msconta.dto.ContaDTO;
import com.bantads.msconta.dto.GerenteDTO;
import com.bantads.msconta.dto.TransacaoDTO;
import com.bantads.msconta.entity.cud.Cliente;
import com.bantads.msconta.entity.cud.Conta;
import com.bantads.msconta.entity.cud.Gerente;
import com.bantads.msconta.entity.r.ContaR;
import com.bantads.msconta.service.ClienteService;
import com.bantads.msconta.service.ContaService;
import com.bantads.msconta.service.GerenteService;
import com.bantads.msconta.util.ConverterParaDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin
@RestController
public class ContaController {

    @Autowired
    private ContaService contaService;
    
    @Autowired
    private ClienteService clienteService;
      
    @Autowired
    private ConverterParaDTO DTOConverter;
       
    
    @PostMapping("/conta/conta/aprovar")
    public ResponseEntity<AnaliseContaDTO> aprovarConta(@RequestBody AnaliseContaDTO analiseConta) {
    	Conta contaAprovada = contaService.aprovarConta(analiseConta.getCpfCliente());
    	
    	if (contaAprovada == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(analiseConta);
        }
    }
    
    @PostMapping("/conta/conta/recusar")
    public ResponseEntity<AnaliseContaDTO> recusarConta(@RequestBody AnaliseContaDTO analiseConta) {
    	Conta contaReprovada = contaService.recusarConta(analiseConta.getCpfCliente());
    	
    	if (contaReprovada == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(analiseConta);
        }
    }
    
    @PostMapping("/conta/conta/transacao")
    public ResponseEntity<TransacaoDTO> transacao(@RequestBody TransacaoDTO transacao) {
        transacao = contaService.transacao(transacao); 
        
        if (transacao.getSuccess() == false) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(transacao);
        }
    }
    
    @GetMapping("/conta/conta/melhoresClientes/{cpf}")
    public ResponseEntity<List<ContaDTO>> buscarTresMelhoreClientesGerente(@PathVariable("cpf") String cpf) {
        List<Conta> contas = contaService.find3MelhoresContasGerente(cpf);
        
        if (contas == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(DTOConverter.converterListaContaParaListaContaDTO(contas));
        }    
    }
    
    @GetMapping("/conta/conta/analise/cpf")
    public ResponseEntity<List<String>> buscarCpfsAnalise() {
        List<Conta> contas = contaService.findByStatusConta(StatusConta.ANALISE);
        List<ContaDTO> contasDTO = DTOConverter.converterListaContaParaListaContaDTO(contas);
        
        if (contasDTO == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(contasDTO.stream()
									                    .map(contaDTO -> contaDTO.getCliente().getCpf()) 
									                    .collect(Collectors.toList()));
        }   
    }
    
    @GetMapping("/conta/conta/analise/cpf/gerente/{cpf}")
    public ResponseEntity<List<String>> buscarCpfsAnalisePorGerente(@PathVariable("cpf") String cpf) {
        List<Conta> contas = contaService.findByStatusContaAndCpfGerente(StatusConta.ANALISE, cpf);
        List<ContaDTO> contasDTO = DTOConverter.converterListaContaParaListaContaDTO(contas);
        
        if (contasDTO == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(contasDTO.stream()
									                    .map(contaDTO -> contaDTO.getCliente().getCpf()) 
									                    .collect(Collectors.toList()));
        }   
    }
    
    @GetMapping("/conta/conta/aprovado/gerente/{cpf}")
    public ResponseEntity<List<ContaDTO>> buscarAprovadoPorGerente(@PathVariable("cpf") String cpf) {
        List<Conta> contas = contaService.findByStatusContaAndCpfGerente(StatusConta.APROVADO, cpf);
        
        if (contas == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(DTOConverter.converterListaContaParaListaContaDTO(contas));
        }
    }
    
    @GetMapping("/conta/conta/aprovado")
    public ResponseEntity<List<ContaDTO>> buscarAprovado() {
        List<Conta> contas = contaService.findByStatusConta(StatusConta.APROVADO);
        
        if (contas == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(DTOConverter.converterListaContaParaListaContaDTO(contas));
        }
    }
    
    @GetMapping("/conta/conta/analise")
    public ResponseEntity<List<ContaDTO>> buscarAnlise() {
        List<Conta> contas = contaService.findByStatusConta(StatusConta.ANALISE);
        
        if (contas == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(DTOConverter.converterListaContaParaListaContaDTO(contas));
        }
    }
    
    @GetMapping("/conta/conta/cliente/{cpf}")
    public ResponseEntity<ContaDTO> buscarPorCpf(@PathVariable("cpf") String cpf) {
        Cliente cliente = clienteService.findByCpf(cpf);
        if (cliente != null) {
            Conta conta = contaService.findByIdCliente(cliente.getId());
            if (conta != null) {
                ContaDTO contaDTO = DTOConverter.converterContaParaContaDTO(conta);
                return ResponseEntity.status(200).body(contaDTO);
            }
        }
        return ResponseEntity.status(400).build();
    }
    
    // Para teste
    
    @GetMapping("/conta/conta/read")
    public ResponseEntity<List<ContaR>> buscarAllRead() {
        List<ContaR> contas = contaService.findAllR();
        
        if (contas == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(contas);
        }
    }
    
    @GetMapping("/conta/conta")
    public ResponseEntity<List<Conta>> buscarAll() {
        List<Conta> contas = contaService.findAll();
        
        if (contas == null) {
        	return ResponseEntity.status(400).build();
        } else {
        	return ResponseEntity.status(200).body(contas);
        }
    }
}
