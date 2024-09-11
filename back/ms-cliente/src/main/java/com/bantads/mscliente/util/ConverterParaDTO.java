package com.bantads.mscliente.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bantads.mscliente.dto.ClienteDTO;
import com.bantads.mscliente.dto.EnderecoDTO;
import com.bantads.mscliente.entity.Cliente;
import com.bantads.mscliente.entity.Endereco;
import com.bantads.mscliente.repository.EnderecoRepository;

@Service
public class ConverterParaDTO {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setCpf(cliente.getCpf());
        dto.setTelefone(cliente.getTelefone());
        dto.setSalario(cliente.getSalario());
        dto.setStatusConta(cliente.getStatusConta());

        Endereco endereco = enderecoRepository.findById(cliente.getIdEndereco()).orElse(null);
        if (endereco != null) {
            dto.setEndereco(convertEnderecoToDTO(endereco));
        }

        return dto;
    }

    private EnderecoDTO convertEnderecoToDTO(Endereco endereco) {
        EnderecoDTO dto = new EnderecoDTO();
        
        dto.setId(endereco.getId());
        dto.setTipo(endereco.getTipo());
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setCidade(endereco.getCidade());
        dto.setBairro(endereco.getBairro());
        dto.setEstado(endereco.getEstado());
        dto.setComplemento(endereco.getComplemento());

        return dto;
    }
}
