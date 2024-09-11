import { Component, ViewChild } from '@angular/core';
import { Cliente, Conta, Endereco, PesquisaCliente, PesquisaConta, PesquisaUsuario, Usuario } from '../../shared';
import { NgForm } from '@angular/forms';
import { ClienteService } from '../services/cliente.service';
import { Router } from '@angular/router';
import { ContaService } from '../services/conta.service';
import { AuthService } from '../../auth/services/auth.service';
import { UsuarioService } from '../services/usuario.service';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent {
  @ViewChild('formPerfil') formAutocadastro!: NgForm;

  usuario: Usuario = new Usuario();
  conta: Conta = new Conta();
  cliente: Cliente = new Cliente();
  cpf: string = '';
  mensagemSucesso: string | null = null;

  constructor(
    private clienteService: ClienteService,
    private contaService: ContaService,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.cpf = this.authService.getCpfFromToken();
    this.buscarContaPorCPf();
    this.buscarClientePorCPf();
    this.buscarUsuarioPorCPf();
  }

  buscarUsuarioPorCPf(): void {
    this.usuarioService.buscarPorCpf(this.cpf).subscribe({
      next: (data: PesquisaUsuario) => {
        if (data == null) {
          console.log('Nenhum usuário encontrado para o CPF fornecido');
        } else {
          this.usuario = data.usuario;
          console.log('Usuário encontrado:', this.usuario);
          console.log('Senha do usuário:', this.usuario.senha);
        }
      }
    });
  }
  

  buscarContaPorCPf(): void {
    this.contaService.buscarPorCpf(this.cpf).subscribe({
      next: (data: PesquisaConta) => {
        if (data == null) {

        } else {
          this.conta = data.conta;
          console.log(this.conta);
        }
      }
    });
  }

  buscarClientePorCPf(): void {
    this.clienteService.buscarPorCpf(this.cpf).subscribe({
      next: (data: PesquisaCliente) => {
        if (data == null) {

        } else {
          this.cliente = data.cliente;
          console.log(this.cliente);
        }
      }
    });
  }

  converterParaFloat(valorEmString: string): number {
    console.log("Valor original:", valorEmString);
    
    let valorFormatado = valorEmString.replace("R$", "").trim();
    valorFormatado = valorFormatado.replace(/\./g, "").replace(",", ".");
    
    const valorFloat = parseFloat(valorFormatado);
    console.log("Valor final em float:", valorFloat);
    
    return valorFloat;
  }
  

  editarPerfil(): void {
    this.cliente.senha = this.usuario.senha;
    console.log('Senha copiada para cliente:', this.cliente.senha);
  
    let valorEmString: string = this.cliente.salario.toString();
    this.cliente.salario = this.converterParaFloat(valorEmString);
  
    this.clienteService.editarPerfil(this.cliente).subscribe({
      next: (data: PesquisaCliente) => {
        if (data == null) {
          console.log('Falha ao editar perfil');
        } else {
          this.cliente = data.cliente;
          console.log('Perfil atualizado:', this.cliente);
          this.abrirMensagem();
          this.buscarUsuarioPorCPf()
        }
      }
    });
  }
  

  abrirMensagem() {
    this.mensagemSucesso = 'Dados salvos com sucesso!';
    console.log(this.mensagemSucesso);
  }
}
