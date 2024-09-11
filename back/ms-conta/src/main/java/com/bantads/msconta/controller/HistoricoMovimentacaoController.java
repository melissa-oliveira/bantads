package com.bantads.msconta.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.msconta.dto.HistoricoMovimentacaoDTO;
import com.bantads.msconta.entity.r.HistoricoMovimentacaoR;
import com.bantads.msconta.service.HistoricoMovimentacaoService;
import com.bantads.msconta.util.ConverterParaDTO;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RestController
public class HistoricoMovimentacaoController {

    @Autowired
    private HistoricoMovimentacaoService historicoMovimentacaoService;
    
    @Autowired
    private ConverterParaDTO DTOConverter;

        
    @PostMapping("/conta/historico")
    public HistoricoMovimentacaoR inserir(@RequestBody HistoricoMovimentacaoR historicoMovimentacao) {
        historicoMovimentacaoService.create(historicoMovimentacao);
        return historicoMovimentacaoService.findById(historicoMovimentacao.getId());        
    }
    
    @GetMapping("/conta/historico")
    public ResponseEntity<List<HistoricoMovimentacaoDTO>> buscar() {
        List<HistoricoMovimentacaoR> historico = historicoMovimentacaoService.findAll();
        if (historico != null && !historico.isEmpty()) {
            List<HistoricoMovimentacaoDTO> dto = historico.stream()
                .map(DTOConverter::converterParaHistoricoMovimentacaoDTO)
                .collect(Collectors.toList());
            return ResponseEntity.status(200).body(dto);
        }
        return ResponseEntity.status(204).build();
    }
    
    @GetMapping("/conta/extrato/{numeroConta}")
    public ResponseEntity<List<HistoricoMovimentacaoDTO>> extrato(
    		@PathVariable("numeroConta") String numeroConta,
            @RequestParam(value = "dataInicio", required = false) String dataInicio,
            @RequestParam(value = "dataFim", required = false) String dataFim) {
        
        List<HistoricoMovimentacaoR> historico;
        if (dataInicio == null || dataInicio == "") {
            dataInicio = "1800-01-01 00:00:00";
        } 
        
        if (dataFim == null || dataFim == "") {
            dataFim = "3000-01-01 00:00:00";
        } 
        
        historico = historicoMovimentacaoService.findByDataHoraBetweenAndNumeroConta(numeroConta, dataInicio, dataFim);
        
        if (historico != null && !historico.isEmpty()) {
            List<HistoricoMovimentacaoDTO> dto = historico.stream()
                .map(DTOConverter::converterParaHistoricoMovimentacaoDTO)
                .collect(Collectors.toList());
            return ResponseEntity.status(200).body(dto);
        }
        
        return ResponseEntity.status(204).build();
    }

    
    @GetMapping("/conta/historico/cliente/{id}")
    public List<HistoricoMovimentacaoDTO> buscarPorCliente(@PathVariable("id") Long id) {
        return historicoMovimentacaoService.findAllByClienteDTO(id);       
    }
    
    @GetMapping("/conta/historico/{id}")
    public HistoricoMovimentacaoR buscarPorId(@PathVariable("id") Long id) {      
        return historicoMovimentacaoService.findById(id);      
    }
 
    @PutMapping("/conta/historico")
    public HistoricoMovimentacaoR atualizar(@RequestBody HistoricoMovimentacaoR historicoMovimentacao) { 
        return historicoMovimentacaoService.update(historicoMovimentacao);           
    }
    
    @DeleteMapping("/conta/historico/{id}")
    public void deletarPorId(@PathVariable("id") Long id) {     
        historicoMovimentacaoService.delete(id);       
    }
    
}
