package com.bantads.saga.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.saga.constant.TipoUsuario;
import com.bantads.saga.dto.ContaDTO;
import com.bantads.saga.dto.GerenteDTO;
import com.bantads.saga.dto.LoginDTO;
import com.bantads.saga.rabbitmq.AuthProducer;
import com.bantads.saga.rabbitmq.AuthTransfer;
import com.bantads.saga.rabbitmq.ClienteProducer;
import com.bantads.saga.rabbitmq.ContaProducer;
import com.bantads.saga.rabbitmq.ContaTransfer;
import com.bantads.saga.rabbitmq.GerenteProducer;
import com.bantads.saga.rabbitmq.GerenteTransfer;
import com.bantads.saga.util.JsonResponse;
import com.bantads.saga.util.ValidaCPF;

@CrossOrigin
@RestController
public class GerenteController {
	
	@Autowired
	private ClienteProducer clienteSender;
	
	@Autowired
	private AuthProducer authSender;
	
	@Autowired
	private ContaProducer contaSender;
	
	@Autowired
	private GerenteProducer gerenteSender;
		
	
	/**
	 * Inserção de Gerente:
	 * Este endpoint é responsável pelo fluxo de remoção de um gerente.
	 * 
	 * Fluxo do Autocadastro:
	 * 1. MS Gerente: Inserir o gerente
	 * 2. MS Auth: Criar usuário do gerente
	 * 3. MS Conta: Atribuir conta ao novo gerente
	 *    
	 * @param gerente Objeto GerenteDTO contendo as informações do gerente.
	 * @return ResponseEntity<Object> contendo a resposta do processo de remoção.
	 */
	@PostMapping(value = "/saga/gerente", produces = "application/json")
	public ResponseEntity<Object> postGerente(@RequestBody GerenteDTO gerente) { 
		
		// PASSO 1 - VALIDAÇÃO DE DADOS
		// Validação de dados do gerente
		if (gerente.getNome() == null || 
				gerente.getCpf() == null || 
				gerente.getEmail() == null ||
				gerente.getSenha() == null ||
				gerente.getTelefone() == null) {
			return new ResponseEntity<>(
					new JsonResponse(false, "Dados do gerente inválidos", null), 
					HttpStatus.BAD_REQUEST);
		}
		
		if (!ValidaCPF.isCPFValido(gerente.getCpf())) {
			return new ResponseEntity<>(
					new JsonResponse(false, "CPF Inválido", null), 
					HttpStatus.BAD_REQUEST);
		}
		
		// PASSO 2 - REALIZAÇÃO DO AUTOCADASTRO
		// Orquestração
		try { 
					
			////////////////////////////////////////
			// 1. MS GERENTE: Insercao do gerente //
			////////////////////////////////////////
									
				// Enviar mensagem
				GerenteTransfer responseGerente = gerenteSender.sendAndReceive(gerente, "gerente-register");
				System.out.println("MS-SAGA LOG (GerenteController): responseGerente - " + responseGerente.getAction());
				
				if (responseGerente.getAction().equals("gerente-ok")) {
				
				//////////////////////////////////////////
				// 2. MS AUTH: Criar usuário do gerente //
				//////////////////////////////////////////
					
					// Adicionar dados de login
					LoginDTO login = new LoginDTO();
					login.setEmail(gerente.getEmail());
					login.setCpf(gerente.getCpf());
					login.setSenha(gerente.getSenha());
					login.setTipo(TipoUsuario.GERENTE);
					
					// Enviar mensagem
					AuthTransfer responseAuth = authSender.sendAndReceive(login, "auth-register");
					System.out.println("MS-SAGA LOG (GerenteController): responseAuth - " + responseAuth.getAction());
					
					if (responseAuth.getAction().equals("auth-ok")) {
		
					//////////////////////////////////////////////////////
					// 3. MS CONTA: Atribuição da conta ao novo gerente //
					//////////////////////////////////////////////////////
						
						// Adicionar dados de conta
						ContaDTO conta = new ContaDTO();
						conta.setGerente(gerente);
						
						// Enviar mensagem
						ContaTransfer responseConta = contaSender.sendAndReceive(conta, "conta-new-gerente");
						System.out.println("MS-SAGA LOG (GerenteController): responseConta - " + responseConta.getAction());
						
						if (responseConta.getAction().equals("conta-ok")) {
							return new ResponseEntity<>(
									new JsonResponse(true, "Gerente cadastrado com sucesso", gerente),
									HttpStatus.OK);
						} 
						// Atribuição de conta a gerente falhou - Não há clientes suficientes
						else if (responseConta.getAction().equals("conta-impossible")) {
							return new ResponseEntity<>(
									new JsonResponse(true, "Gerente cadastrado com sucesso", gerente),
									HttpStatus.OK);
						} 
						// Atribuição de conta a gerente falhou - Erro genérico
						else if (responseConta.getAction().equals("conta-failed")) {
							// Desfazer ações
							gerenteSender.sendAndReceive(gerente, "gerente-delete");
							authSender.sendAndReceive(login, "auth-delete");
							
							return new ResponseEntity<>(
									new JsonResponse(false, "Erro ao atribuir conta a gerente", null),
									HttpStatus.INTERNAL_SERVER_ERROR);
						} 
						// Atribuição de conta a gerente falhou - Ação não reconhecida
						else {
							// Desfazer ações
							gerenteSender.sendAndReceive(gerente, "gerente-delete");
							authSender.sendAndReceive(login, "auth-delete");
							
							System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseConta.getAction());
							return new ResponseEntity<>(
									new JsonResponse(false, "Erro ao atribuir conta a gerente", null),
									HttpStatus.BAD_REQUEST);
						}
		
					/////////////////////
					// 3. END MS CONTA //
					/////////////////////
						
					} 
					// Registro de usuário de gerente falhou - Email já cadastrado
					else if (responseAuth.getAction().equals("auth-failed/email-registered")) {
						// Desfazer ações
						gerenteSender.sendAndReceive(gerente, "gerente-delete");
						
						return new ResponseEntity<>(
								new JsonResponse(false, "Email ja cadastrado", null),
								HttpStatus.BAD_REQUEST);
					} 
					// Registro de usuário de gerente falhou - Erro genérico
					else if (responseAuth.getAction().equals("auth-failed")) {
						// Desfazer ações
						gerenteSender.sendAndReceive(gerente, "gerente-delete");
						
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao criar usuário de gerente", null),
								HttpStatus.INTERNAL_SERVER_ERROR);
					} 
					// Registro de usuário de gerente falhou - Ação não reconhecida
					else {
						// Desfazer ações
						gerenteSender.sendAndReceive(gerente, "gerente-delete");
						
						System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseAuth.getAction());
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao criar usuario de gerente", null),
								HttpStatus.BAD_REQUEST);
					}
		
				////////////////////
				// 2. END MS AUTH //
				////////////////////
					
				} 
				// Registro de gerente falhou - Email já cadastrado
				else if (responseGerente.getAction().equals("gerente-failed/email-registered")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "Email ja cadastrado", null),
							HttpStatus.BAD_REQUEST);
				} 
				// Registro de gerente falhou - CPF já cadastrado
				else if (responseGerente.getAction().equals("gerente-failed/cpf-registered")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "CPF ja cadastrado", null),
							HttpStatus.BAD_REQUEST);							
				} 
				// Registro de gerente falhou - Erro genérico
				else if (responseGerente.getAction().equals("gerente-failed")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao criar gerente", null),
							HttpStatus.INTERNAL_SERVER_ERROR);
				} 
				// Registro de gerente falhou - Ação não reconhecida
				else {
					System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseGerente.getAction());
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao criar gerente", null),
							HttpStatus.BAD_REQUEST);
				}
					
			///////////////////////
			// 1. END MS GERENTE //
			///////////////////////

		} 
		// Erro genérico ao realizar a SAGA
		catch (Exception e) {
			System.out.println("MS-SAGA LOG (GerenteController): Erro na saga de insercao de gerente - " + e.getLocalizedMessage());
			return new ResponseEntity<>(
					new JsonResponse(false, "Erro no servidor ao realizar inercao de gerente", null),
					HttpStatus.INTERNAL_SERVER_ERROR
				);
		}
	}
	
	
	/**
	 * Remoção de Gerente:
	 * Este endpoint é responsável pelo fluxo de remoção de um gerente.
	 * 
	 * Fluxo do Autocadastro:
	 * 1. MS Conta: Atribuir contas a um novo gerente com menos contas
	 * 2. MS Gerente: Remover gerente
	 * 3. MS Auth: Remover usuário do gerente
	 *    
	 * @param cpf String contendo o email do gerente.
	 * @return ResponseEntity<Object> contendo a resposta do processo de remoção.
	 */
	@DeleteMapping("/saga/gerente/{cpf}")
	public ResponseEntity<Object> deleteGerente(@PathVariable("cpf") String cpf) { 
		
		// Orquestração
		try { 
			////////////////////////////////////////////////////
			// 1. MS CONTA: Atribuir contas a um novo gerente //
			////////////////////////////////////////////////////
			
			// Adicionar dados de conta
			ContaDTO conta = new ContaDTO();
			GerenteDTO gerente = new GerenteDTO();
			
			gerente.setCpf(cpf);
			conta.setGerente(gerente);
			
			// Enviar mensagem
			ContaTransfer responseConta = contaSender.sendAndReceive(conta, "conta-delete-gerente");
			System.out.println("MS-SAGA LOG (GerenteController): responseConta - " + responseConta.getAction());
			
			if (responseConta.getAction().equals("conta-ok/gerente-deleted")) {
				
				////////////////////////////////////
				// 2. MS GERENTE: Remover gerente //
				////////////////////////////////////
								
				// Enviar mensagem
				GerenteTransfer responseGerente = gerenteSender.sendAndReceive(gerente, "gerente-delete");
				System.out.println("MS-SAGA LOG (GerenteController): responseGerente - " + responseGerente.getAction());
				gerente = responseGerente.getGerente();
				
				if (responseGerente.getAction().equals("gerente-deleted")) {
			
					/////////////////////////////////////////////
					// 3. MS AUTH:  Remover usuário do gerente //
					/////////////////////////////////////////////
					
					// Adicionar dados de usuário
					LoginDTO login = new LoginDTO();
					login.setEmail(gerente.getEmail());
					
					// Enviar mensagem
					AuthTransfer responseAuth = authSender.sendAndReceive(login, "auth-delete");
					System.out.println("MS-SAGA LOG (GerenteController): responseAuth - " + responseAuth.getAction());
					
					if (responseAuth.getAction().equals("auth-deleted")) {
						return new ResponseEntity<>(
								new JsonResponse(true, "Gerente removido com sucesso", gerente),
								HttpStatus.OK);
					} 					
					// Remoção de usuário falhou - Erro genérico
					else if (responseAuth.getAction().equals("auth-failed")) {
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao remover usuario de gerente", null),
								HttpStatus.INTERNAL_SERVER_ERROR);							
					} 
					// Remoção de usuário falhou - Ação não reconhecida
					else {
						System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseAuth.getAction());
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao remover usuario de gerente", null),
								HttpStatus.BAD_REQUEST);
					}
			
					////////////////////
					// 3. END MS AUTH //
					////////////////////

				} 
				// Remoção de gerente falhou - Erro genérico
				else if (responseConta.getAction().equals("gerente-failed")) {
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao remover gerente", null),
							HttpStatus.INTERNAL_SERVER_ERROR);							
				} 
				// Remoção de gerente falhou - Ação não reconhecida
				else {
					System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseGerente.getAction());
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao remover gerente", null),
							HttpStatus.BAD_REQUEST);
				}
							
				///////////////////////
				// 2. END MS GERENTE //
				///////////////////////
				
			} 
			// Remoção de gerente de contas falhou - Ação não reconhecida
			else if (responseConta.getAction().equals("conta-failed/last-gerente")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "O ultimo gerente nao pode ser removido", null),
						HttpStatus.BAD_REQUEST);							
			} 
			// Remoção de gerente de contas falhou - Erro genérico
			else if (responseConta.getAction().equals("conta-failed")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao atribuir contas a novo gerente", null),
						HttpStatus.INTERNAL_SERVER_ERROR);							
			} 
			// Remoção de gerente de contas falhou - Ação não reconhecida
			else {
				System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseConta.getAction());
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao atribuir contas a novo gerente", null),
						HttpStatus.BAD_REQUEST);
			}
			
			/////////////////////
			// 1. END MS CONTA //
			/////////////////////
		} 
		// Erro genérico ao realizar a SAGA
		catch (Exception e) {
			System.out.println("MS-SAGA LOG (GerenteController): Erro na saga de remocao de gerente - " + e.getLocalizedMessage());
			return new ResponseEntity<>(
					new JsonResponse(false, "Erro no servidor ao realizar remocao de gerente", null),
					HttpStatus.INTERNAL_SERVER_ERROR
				);
		}
	}
	
	/**
	 * Edição de Gerente:
	 * Este endpoint é responsável pelo fluxo de edição de um gerente.
	 * 
	 * Fluxo do Autocadastro:
	 * 1. MS Gerente: Editar dados de gerente
	 * 2. MS Auth: Editar dados do gerente
	 * 3. MS Conta: Editar dados do gerente
	 *    
	 * @param gerente GerenteDTO contendo o gerente.
	 * @return ResponseEntity<Object> contendo a resposta do processo de remoção.
	 */
	@PutMapping(value = "/saga/gerente", produces = "application/json")
	public ResponseEntity<Object> putGerente(@RequestBody GerenteDTO gerente) { 
		
		// PASSO 1 - VALIDAÇÃO DE DADOS
		// Validação de dados do gerente
		if (gerente.getNome() == null || 
				gerente.getCpf() == null || 
				gerente.getEmail() == null ||
				gerente.getSenha() == null ||
				gerente.getTelefone() == null) {
			return new ResponseEntity<>(
					new JsonResponse(false, "Dados do gerente inválidos", null), 
					HttpStatus.BAD_REQUEST);
		}
		
		if (!ValidaCPF.isCPFValido(gerente.getCpf())) {
			return new ResponseEntity<>(
					new JsonResponse(false, "CPF Inválido", null), 
					HttpStatus.BAD_REQUEST);
		}
		
		// PASSO 2 - REALIZAÇÃO DO AUTOCADASTRO
		// Orquestração
		try { 
			///////////////////////////////////
			// 1. MS GERENTE: Editar gerente //
			///////////////////////////////////
			
			// Enviar mensagem
			GerenteTransfer responseGerente = gerenteSender.sendAndReceive(gerente, "gerente-edit");
			System.out.println("MS-SAGA LOG (GerenteController): responseGerente - " + responseGerente.getAction());
			
			if (responseGerente.getAction().equals("gerente-ok")) {
				
				////////////////////////////////////////////
				// 2. MS AUTH:  Editar usuário do gerente //
				////////////////////////////////////////////
				
				// Adicionar dados de login
				LoginDTO login = new LoginDTO();
				login.setEmail(gerente.getEmail());
				login.setCpf(gerente.getCpf());
				login.setSenha(gerente.getSenha());
				login.setTipo(TipoUsuario.GERENTE);
				
				// Enviar mensagem
				AuthTransfer responseAuth = authSender.sendAndReceive(login, "auth-edit");
				System.out.println("MS-SAGA LOG (GerenteController): responseAuth - " + responseAuth.getAction());
				
				if (responseAuth.getAction().equals("auth-ok")) {
										
					//////////////////////////////////
					// 3. MS CONTA:  Editar gerente //
					//////////////////////////////////
					
					// Adicionar dados de conta
					ContaDTO conta = new ContaDTO();
					conta.setGerente(gerente);
					
					// Enviar mensagem
					ContaTransfer responseConta = contaSender.sendAndReceive(conta, "conta-edit-gerente");
					System.out.println("MS-SAGA LOG (GerenteController): responseConta - " + responseConta.getAction());
			
					if (responseConta.getAction().equals("conta-ok")) {
						return new ResponseEntity<>(
								new JsonResponse(true, "Gerente editado com sucesso", gerente),
								HttpStatus.OK);
					}
					// Edição de gerente falhou - Gerente não encontrado
					else if (responseConta.getAction().equals("conta-failed/gerente-nonexistent")) {
						return new ResponseEntity<>(
								new JsonResponse(false, "Usuário não encontrado", null),
								HttpStatus.BAD_REQUEST);							
					} // Edição de gerente falhou - Erro genérico
					else if (responseConta.getAction().equals("conta-failed")) {
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao editar gerente", null),
								HttpStatus.INTERNAL_SERVER_ERROR);
					} 
					// Edição de gerente falhou - Ação não reconhecida
					else {
						System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseAuth.getAction());
						return new ResponseEntity<>(
								new JsonResponse(false, "Erro ao editar gerente", null),
								HttpStatus.BAD_REQUEST);
					}
					/////////////////////
					// 3. END MS CONTA //
					/////////////////////
						
				////////////////////
				// 2. END MS AUTH //
				////////////////////
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
					System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseGerente.getAction());
					return new ResponseEntity<>(
							new JsonResponse(false, "Erro ao editar usuário", null),
							HttpStatus.BAD_REQUEST);
				}
			
			///////////////////////
			// 1. END MS GERENTE //
			///////////////////////
			} 
			// Edição de gerente falhou - Gerente não encontrado
			else if (responseGerente.getAction().equals("gerente-failed/gerente-nonexistent")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Gerente não encontrado", null),
						HttpStatus.BAD_REQUEST);							
			} 
			// Edição de gerente falhou - Email já cadastrado
			else if (responseGerente.getAction().equals("gerente-failed/email-registered")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Email ja cadastrado", null),
						HttpStatus.BAD_REQUEST);
			} 
			// Edição de gerente falhou - CPF já cadastrado
			else if (responseGerente.getAction().equals("gerente-failed/cpf-registered")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "CPF ja cadastrado", null),
						HttpStatus.BAD_REQUEST);							
			} 
			// Edição de gerente falhou - Erro genérico
			else if (responseGerente.getAction().equals("gerente-failed")) {
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao editar gerente", null),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} 
			// Edição de gerente falhou - Ação não reconhecida
			else {
				System.out.println("MS-SAGA LOG (GerenteController): Acao nao reconhecida - " + responseGerente.getAction());
				return new ResponseEntity<>(
						new JsonResponse(false, "Erro ao editar gerente", null),
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			System.out.println("MS-SAGA LOG (GerenteController): Erro na saga de edicao de gerente - " + e.getLocalizedMessage());
			return new ResponseEntity<>(
					new JsonResponse(false, "Erro no servidor ao realizar edição de gerente", null),
					HttpStatus.INTERNAL_SERVER_ERROR
				);
		}
	}
}
