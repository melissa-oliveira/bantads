package com.bantads.msconta.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bantads.msconta.dto.HistoricoMovimentacaoDTO;
import com.bantads.msconta.entity.r.ContaR;
import com.bantads.msconta.entity.r.HistoricoMovimentacaoR;
import com.bantads.msconta.repository.r.ContaRRepository;
import com.bantads.msconta.repository.r.HistoricoMovimentacaoRRepository;
import com.bantads.msconta.util.ConverterParaDTO;

@Service
@Transactional
public class HistoricoMovimentacaoService {

    @Autowired
    HistoricoMovimentacaoRRepository historicoReadRepository;
    
    @Autowired
    private ContaRRepository contaReadRepository;
    
    @Autowired
    private ConverterParaDTO DTOConverter;

    public List<HistoricoMovimentacaoR> findAll() {		
        return historicoReadRepository.findAll();
    }
    
    public List<HistoricoMovimentacaoDTO> findAllByClienteDTO(Long id) {
        List<HistoricoMovimentacaoR> historicos = historicoReadRepository.findAllByIdClienteOrigemOrIdClienteDestino(id, id);
        return historicos.stream()
                         .map(DTOConverter::converterParaHistoricoMovimentacaoDTO)
                         .collect(Collectors.toList());
    }

    public HistoricoMovimentacaoR findById(Long id) {		
        return historicoReadRepository.findById(id).orElse(null);	
    }
    
    public List<HistoricoMovimentacaoR> findByDataHoraBetweenAndNumeroConta(String numeroConta, String inicio, String fim) {
    	ContaR conta = contaReadRepository.findByNumeroConta(numeroConta);
    	Long idCliente = conta.getCliente().getId();
    	return historicoReadRepository.findAllByDataHoraBetweenAndIdClienteOrigemOrIdClienteDestino(
    			inicio, fim, idCliente, idCliente);
    }

    public HistoricoMovimentacaoR create(HistoricoMovimentacaoR entity) {		
        return historicoReadRepository.save(entity);
    }

    public HistoricoMovimentacaoR update(HistoricoMovimentacaoR entity) {
    	HistoricoMovimentacaoR existingHistoricoMovimentacao = historicoReadRepository.findById(entity.getId()).orElse(null);
        if (existingHistoricoMovimentacao != null) {
            existingHistoricoMovimentacao.setDataHora(entity.getDataHora());
            existingHistoricoMovimentacao.setTipoMovimentacao(entity.getTipoMovimentacao());
            existingHistoricoMovimentacao.setValor(entity.getValor());
            existingHistoricoMovimentacao.setIdClienteOrigem(entity.getIdClienteOrigem());
            existingHistoricoMovimentacao.setIdClienteDestino(entity.getIdClienteDestino());

            HistoricoMovimentacaoR historicoMovimentacaoAtualizada = historicoReadRepository.save(existingHistoricoMovimentacao);
            return historicoMovimentacaoAtualizada;
        } else {
            return null;
        }
    }

    public void delete(Long id) {
        historicoReadRepository.deleteById(id);
    }
}
