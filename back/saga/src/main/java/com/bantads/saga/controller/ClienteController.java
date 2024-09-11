package com.bantads.saga.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.saga.constant.TipoUsuario;
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
public class ClienteController {
	
	@Autowired
	private ClienteProducer clienteSender;
	
	@Autowired
	private AuthProducer authSender;
	
	@Autowired
	private ContaProducer contaSender;


	/**
	 * Alteração de Perfil de Cliente:
	 * Este endpoint é responsável pelo fluxo de alteração de perfil de um cliente.
	 * 
	 * Fluxo do Autocadastro:
	 * 1. MS Cliente: Alteração dos dados do registro de Cliente
	 * 2. MS Auth: Alterar dados do cliente
	 * 3. MS Conta: Cálculo e alteração do novo limite da Conta + alterar dados do cliente
	 *    
	 * @param cliente Objeto ClienteDTO contendo as informações do cliente para autocadastro.
	 * @return ResponseEntity<Object> contendo a resposta do processo de alteração.
	 */
	@PutMapping(value = "/saga/cliente", produces = "application/json")
	public ResponseEntity<Object> putCliente(@RequestBody ClienteDTO cliente) {
		
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
			
			// Enviar mensagem
			ClienteTransfer responseCliente = clienteSender.sendAndReceive(cliente, "cliente-edit");
			System.out.println("MS-SAGA LOG (ClienteController): responseCliente - " + responseCliente.getAction());
			
			if (responseCliente.getAction().equals("cliente-ok")) {
								
				////////////////////////////////////////////////////////////////
				// 2. MS AUTH: Criação do registro de autenticação do cliente //
				////////////////////////////////////////////////////////////////
				// Adicionar dados de login
				LoginDTO login = new LoginDTO();
				login.setEmail(cliente.getEmail());
				login.setCpf(cliente.getCpf());
				login.setSenha(cliente.getSenha());
				login.setTipo(TipoUsuario.CLIENTE);
				
				// Enviar mensagem
				AuthTransfer responseAuth = authSender.sendAndReceive(login, "auth-edit");
				System.out.println("MS-SAGA LOG (ClienteController): responseAuth - " + responseAuth.getAction());
				
				if (responseAuth.getAction().equals("auth-ok")) {
						
					//////////////////////////////////////////////////////////
					// 3. MS CONTA: Criação do registro de conta do cliente //
					//////////////////////////////////////////////////////////
					// Adicionar dados de conta
					ContaDTO conta = new ContaDTO();
					conta.setCliente(cliente);
					
					if (cliente.getSalario() >= 2000) {
						Double novoLimite = cliente.getSalario() / 2;
						conta.setLimite(novoLimite);
					} else {
						conta.setLimite(0.0);
					}
						
					
					// Enviar mensagem
					ContaTransfer responseConta = contaSender.sendAndReceive(conta, "conta-edit-cliente");
					System.out.println("MS-SAGA LOG (ClienteController): responseConta - " + responseConta.getAction());
			
					if (responseConta.getAction().equals("conta-ok")) {
						return new ResponseEntity<>(
								new JsonResponse(true, "Cliente editado com sucesso", cliente),
								HttpStatus.OK);
					}
					// Edição de gerente falhou - Gerente não encontrado
					else if (responseConta.getAction().equals("conta-failed/cliente-nonexistent")) {
						return new ResponseEntity<>(
								new JsonResponse(false, "Usuário não encontrado", null),
								HttpStatus.BAD_REQUEST);							
					} // Edição de gerente falhou - Erro genérico
					else if (responseConta.getAction().equals("conta-failed")) {
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao editar cliente", null),
								HttpStatus.INTERNAL_SERVER_ERROR);
					} 
					// Edição de gerente falhou - Ação não reconhecida
					else {
						System.out.println("MS-SAGA LOG (ClienteController): Acao nao reconhecida - " + responseAuth.getAction());
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao editar cliente", null),
								HttpStatus.BAD_REQUEST);
					}
			
					/////////////////////
					// 3. END MS CONTA //
					/////////////////////
				}
				// Edição de usuario falhou - Usuário não encontrado
				else if (responseAuth.getAction().equals("auth-failed/user-nonexistent")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "Usuário não encontrado", null),
							HttpStatus.BAD_REQUEST);							
				} 
				// Edição de usuário falhou - Email já cadastrado
				else if (responseAuth.getAction().equals("auth-failed/email-registered")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "Email ja cadastrado", null),
							HttpStatus.BAD_REQUEST);
				} 
				// Edição de usuário falhou - CPF já cadastrado
				else if (responseAuth.getAction().equals("auth-failed/cpf-registered")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "CPF ja cadastrado", null),
							HttpStatus.BAD_REQUEST);							
				} 
				// Edição de usuário falhou - Erro genérico
				else if (responseAuth.getAction().equals("auth-failed")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao editar usuário", null),
							HttpStatus.INTERNAL_SERVER_ERROR);
				} 
				// Edição de usuário falhou - Ação não reconhecida
				else {
					System.out.println("MS-SAGA LOG (ClienteController): Acao nao reconhecida - " + responseAuth.getAction());
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao editar usuário", null),
							HttpStatus.BAD_REQUEST);
				}
				////////////////////
				// 2. END MS AUTH //
				////////////////////
			} 
			// Edição de gerente falhou - Gerente não encontrado
			else if (responseCliente.getAction().equals("cliente-failed/cliente-nonexistent")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Cliente não encontrado", null),
						HttpStatus.BAD_REQUEST);							
			} 
			// Edição de gerente falhou - Email já cadastrado
			else if (responseCliente.getAction().equals("cliente-failed/email-registered")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Email ja cadastrado", null),
						HttpStatus.BAD_REQUEST);
			} 
			// Edição de gerente falhou - CPF já cadastrado
			else if (responseCliente.getAction().equals("cliente-failed/cpf-registered")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "CPF ja cadastrado", null),
						HttpStatus.BAD_REQUEST);							
			} 
			// Registro de cliente falhou - Erro genérico
			else if (responseCliente.getAction().equals("cliente-failed")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao editar cliente", null),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} 
			// Registro de cliente falhou - Ação não reconhecida
			else {
				System.out.println("MS-SAGA LOG (ClienteController): Acao nao reconhecida - " + responseCliente.getAction());
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao alterar cliente", null),
						HttpStatus.BAD_REQUEST);
			}
			
			///////////////////////
			// 3. END MS CLIENTE //
			///////////////////////
			
		}
		// Erro genérico ao realizar SAGA
		catch (Exception e)  {
			System.out.println("MS-SAGA LOG (ClienteController): Erro na saga de alterar cliente - " + e.getLocalizedMessage());
			return new ResponseEntity<>(
					new JsonResponse(false, "Erro no servidor ao realizar alteração no cliente", null),
					HttpStatus.INTERNAL_SERVER_ERROR
				);
		}
		
	}
}
