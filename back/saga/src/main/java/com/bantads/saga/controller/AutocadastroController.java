package com.bantads.saga.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.saga.constant.StatusConta;
import com.bantads.saga.dto.ClienteDTO;
import com.bantads.saga.dto.ContaDTO;
import com.bantads.saga.dto.EnderecoDTO;
import com.bantads.saga.dto.LoginDTO;
import com.bantads.saga.rabbitmq.AuthProducer;
import com.bantads.saga.rabbitmq.AuthTransfer;
import com.bantads.saga.rabbitmq.ClienteProducer;
import com.bantads.saga.rabbitmq.ClienteTransfer;
import com.bantads.saga.rabbitmq.ContaProducer;
import com.bantads.saga.rabbitmq.ContaTransfer;
import com.bantads.saga.util.JsonResponse;
import com.bantads.saga.util.ValidaCPF;

@CrossOrigin
@RestController
public class AutocadastroController {
	
	@Autowired
	private ClienteProducer clienteSender;
	
	@Autowired
	private AuthProducer authSender;
	
	@Autowired
	private ContaProducer contaSender;
	
	
	
	/**
	 * Autocadastro:
	 * Este endpoint é responsável pelo fluxo de autocadastro de um cliente.
	 * 
	 * Fluxo do Autocadastro:
	 * 1. MS Cliente: Criação do registro de Cliente
	 * 2. MS Autenticação: Criação do registro de autenticação do cliente
	 * 3. MS Conta: Criação do registro de conta do cliente + 
	 *              Consulta de gerentes e contas para decidir o gerente que assume a nova conta.
	 *    
	 * @param cliente Objeto ClienteDTO contendo as informações do cliente para autocadastro.
	 * @return ResponseEntity<Object> contendo a resposta do processo de autocadastro.
	 */
	@PostMapping(value = "/saga/autocadastro", produces = "application/json")
	public ResponseEntity<Object> postCliente(@RequestBody ClienteDTO cliente) {
		
		// PASSO 1 - VALIDAÇÃO DE DADOS
		// Validação de dados do cliente
		if (cliente.getNome() == null || 
				cliente.getCpf() == null || 
				cliente.getEndereco() == null) {
			return new ResponseEntity<>(
					new JsonResponse(false, "Dados do cliente inválidos", null), 
					HttpStatus.BAD_REQUEST);
		}
		
		if (cliente.getEmail() == null) {
			return new ResponseEntity<>(
					new JsonResponse(false, "Email é obrigatório", null),
					HttpStatus.BAD_REQUEST);
		}
		
		if (cliente.getSenha() == null) {
			return new ResponseEntity<>(
					new JsonResponse(false, "Senha é obrigatória", null),
					HttpStatus.BAD_REQUEST);
		}
		
		if (!ValidaCPF.isCPFValido(cliente.getCpf())) {
			return new ResponseEntity<>(
					new JsonResponse(false, "CPF Inválido", null), 
					HttpStatus.BAD_REQUEST);
		}
		
		// Validação de dados do endereço do cliente
		EnderecoDTO endereco = cliente.getEndereco();

		if (endereco.getLogradouro() == null || 
				endereco.getBairro() == null || 
				endereco.getNumero() == null || 
				endereco.getCidade() == null || 
				endereco.getEstado() == null || 
				endereco.getCep() == null) {
			return new ResponseEntity<>(
					new JsonResponse(false, "Dados do endereço do cliente faltantes", null), 
					HttpStatus.BAD_REQUEST);
		}
		
		// PASSO 2 - REALIZAÇÃO DO AUTOCADASTRO
		// Orquestração
		try {
			
			///////////////////////////////////////////////////
			// 1. MS CLIENTE: Criação do registro de Cliente //
			///////////////////////////////////////////////////
			ClienteTransfer responseCliente = clienteSender.sendAndReceive(cliente, "cliente-register");
			System.out.println("MS-SAGA LOG (SagaController): responseCliente - " + responseCliente.getAction());
			
			// Registro de cliente realizado com sucesso
			if (responseCliente.getAction().equals("cliente-ok")) {
				Long clienteID = responseCliente.getCliente().getId();
			
				String senha = cliente.getSenha();
				String email = cliente.getEmail();
				String cpf = cliente.getCpf();
				
				////////////////////////////////////////////////////////////////
				// 2. MS AUTH: Criação do registro de autenticação do cliente //
				////////////////////////////////////////////////////////////////
				LoginDTO loginData = new LoginDTO();
				loginData.setCpf(cpf);
				loginData.setEmail(email);
				loginData.setSenha(senha);
				
				AuthTransfer responseAuth = authSender.sendAndReceive(loginData, "auth-register");
				System.out.println("MS-SAGA LOG (SagaController): responseAuth - " + responseAuth.getAction());
				
				// Registro de usuário realizado com sucesso
				if (responseAuth.getAction().equals("auth-ok")) {
					
					//////////////////////////////////////////////////////////
					// 3. MS CONTA: Criação do registro de conta do cliente //
					//////////////////////////////////////////////////////////
					ContaDTO contaData = new ContaDTO();
					contaData.setCliente(cliente);
					contaData.setStatusConta(StatusConta.ANALISE);
					
					if (cliente.getSalario() >= 2000) {
						contaData.setLimite(cliente.getSalario() / 2);
					} else {
						contaData.setLimite(0.0);
					}
					
					ContaTransfer responseConta = contaSender.sendAndReceive(contaData, "conta-register");
					System.out.println("MS-SAGA LOG (SagaController): responseConta - " + responseConta.getAction());
					
					// Registro de conta realizado com sucesso
					if (responseConta.getAction().equals("conta-ok")) {
						return new ResponseEntity<>(
								new JsonResponse(true, "Conta criada com sucesso", cliente),
								HttpStatus.OK);
					} 
					// Registro de conta falhou - Número de conta já registrado
					else if (responseAuth.getAction().equals("conta-failed/numero-conta-registered")) {
						clienteSender.sendAndReceive(cliente, "cliente-delete");
						authSender.sendAndReceive(loginData, "auth-delete");
						return new ResponseEntity<>(
								new JsonResponse(false, "Numero de conta ja cadastrado", null),
								HttpStatus.BAD_REQUEST);
					} 
					// Registro de conta falhou - Cliente já possuí conta
					else if (responseAuth.getAction().equals("conta-failed/cliente-registered")) {
						clienteSender.sendAndReceive(cliente, "cliente-delete");
						authSender.sendAndReceive(loginData, "auth-delete");
						return new ResponseEntity<>(
								new JsonResponse(false, "Cliente ja possui conta", null),
								HttpStatus.BAD_REQUEST);
					} 
					// Registro de conta falhou - Falha ao selecionar gerenete
					else if (responseAuth.getAction().equals("conta-failed/gerente-failed")) {
						clienteSender.sendAndReceive(cliente, "cliente-delete");
						authSender.sendAndReceive(loginData, "auth-delete");
						return new ResponseEntity<>(
								new JsonResponse(false, "Falha ao selecionar gerente para conta", null),
								HttpStatus.INTERNAL_SERVER_ERROR);
					} 
					// Registro de conta falhou - Falha ao salvar cliente
					else if (responseAuth.getAction().equals("conta-failed/cliente-failed")) {
						clienteSender.sendAndReceive(cliente, "cliente-delete");
						authSender.sendAndReceive(loginData, "auth-delete");
						return new ResponseEntity<>(
								new JsonResponse(false, "Falha ao salvar cliente na conta", null),
								HttpStatus.INTERNAL_SERVER_ERROR);
					} 
					// Registro de conta falhou - Erro genérico
					else if (responseAuth.getAction().equals("conta-failed")) {
						clienteSender.sendAndReceive(cliente, "cliente-delete");
						authSender.sendAndReceive(loginData, "auth-delete");
						return new ResponseEntity<>(
								new JsonResponse(false, "Falha ao criar conta", null),
								HttpStatus.INTERNAL_SERVER_ERROR);
					} 
					// Registro de conta falhou - Ação não reconhecida
					else {
						System.out.println("MS-SAGA LOG (SagaController): Acao nao reconhecida - " + responseConta.getAction());
						clienteSender.sendAndReceive(cliente, "cliente-delete");
						authSender.sendAndReceive(loginData, "auth-delete");
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao criar conta", null),
								HttpStatus.BAD_REQUEST);
			        }
					/////////////////////
					// 3. END MS CONTA //
					/////////////////////
				
				// Registro de usuário falhou - Email já cadastrado
				} else if (responseAuth.getAction().equals("auth-failed/email-registered")) {
					clienteSender.sendAndReceive(cliente, "cliente-delete");
					return new ResponseEntity<>(
							new JsonResponse(false, "Email ja cadastrado, informe outro email", null),
							HttpStatus.BAD_REQUEST);
				} 
				// Registro de usuário falhou - Erro genérico
				else if (responseAuth.getAction().equals("auth-failed")) {
					clienteSender.sendAndReceive(cliente, "cliente-delete");
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao criar usuario", null),
							HttpStatus.INTERNAL_SERVER_ERROR);
				} 
				// Registro de usuário falhou - Ação não reconhecida
				else {
					clienteSender.sendAndReceive(cliente, "cliente-delete");
					System.out.println("MS-SAGA LOG (SagaController): Acao nao reconhecida - " + responseAuth.getAction());
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao cadastrar usuario", null),
							HttpStatus.BAD_REQUEST);
		        }
				////////////////////
				// 2. END MS AUTH //
				////////////////////
				
			} 
			
			// Registro de cliente falhou - CPF já cadastrado
			else if (responseCliente.getAction().equals("cliente-failed/cpf-registered")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "CPF ja cadastrado, informe outro CPF", null),
						HttpStatus.BAD_REQUEST);
			} 
			// Registro de cliente falhou - Erro genérico
			else if (responseCliente.getAction().equals("cliente-failed")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao cadastrar cliente", null),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} 
			// Registro de cliente falhou - Ação não reconhecida
			else {
				System.out.println("MS-SAGA LOG (SagaController): Acao nao reconhecida - " + responseCliente.getAction());
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao cadastrar cliente", null),
						HttpStatus.BAD_REQUEST
					);
			}
			///////////////////////
			// 3. END MS CLIENTE //
			///////////////////////
					
		} 
		// Erro genérico ao realizar SAGA
		catch (Exception e)  {
			System.out.println("MS-SAGA LOG (SagaController): Erro na saga de autocadastro - " + e.getLocalizedMessage());
			return new ResponseEntity<>(
					new JsonResponse(false, "Erro no servidor ao realizar autocadastro", null),
					HttpStatus.INTERNAL_SERVER_ERROR
				);
		}		
	}	
		
}
