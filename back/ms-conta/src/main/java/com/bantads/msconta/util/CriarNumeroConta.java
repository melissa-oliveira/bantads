package com.bantads.msconta.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class CriarNumeroConta {

	public String criarNumeroContaBaseadoId(Long id) {
        // Obter a data e hora atual em formato específico (nano para evitar duplicidade)
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        
        // Combinar ID e data/hora para gerar um valor único
        String base = id + dataHora;

        // Gerar uma parte aleatória para garantir a unicidade
        Random random = new Random();
        int randomPart = random.nextInt(1000); // Gera um número entre 0 e 999

        // Obter um hash da string combinada para garantir que o número não seja diretamente previsível
        int hash = Math.abs(base.hashCode());

        // Formatar o hash e a parte aleatória no formato xxxxx-xxx
        String parte1 = String.format("%05d", hash % 100000);  // Pegar os primeiros 5 dígitos
        String parte2 = String.format("%03d", randomPart);      // Usar a parte aleatória

        // Combinar as partes para formar o número da conta
        String numeroConta = parte1 + "-" + parte2;
        
        return numeroConta;
	}
	
	public String criarNumeroContaBaseadoDataHora() {
        // Obter a data e hora atual em formato específico (nano para garantir unicidade)
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // Criar um hash da string para garantir a unicidade
        int hash = Math.abs(dataHora.hashCode());

        // Formatar o hash em duas partes: xxxxx-xxx
        String parte1 = String.format("%05d", hash % 100000);  // Pegar os primeiros 5 dígitos
        String parte2 = String.format("%03d", hash % 1000);    // Pegar os últimos 3 dígitos

        // Combinar as partes para formar o número da conta
        String numeroConta = parte1 + "-" + parte2;
        
        return numeroConta;
	}
}
