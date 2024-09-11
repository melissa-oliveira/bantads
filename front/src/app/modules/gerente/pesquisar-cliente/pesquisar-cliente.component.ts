import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Cliente, Conta, PesquisaCliente, PesquisaConta } from '../../shared';
import { ClienteService } from '../services/cliente.service';
import { AuthService } from '../../auth/services/auth.service';
import { ContaService } from '../services/conta.service';

declare var bootstrap: any;

@Component({
  selector: 'app-pesquisar-cliente',
  templateUrl: './pesquisar-cliente.component.html',
  styleUrls: ['./pesquisar-cliente.component.css']
})
export class PesquisarClienteComponent implements OnInit {

  @ViewChild('modalCliente') modalCliente!: ElementRef;
  @ViewChild('formPesquisarCliente') formPesquisarCliente!: NgForm;
  mensagemErro: string | null = null;
  conta: Conta = new Conta();
  cliente: Cliente = new Cliente();

  constructor(
    private clienteService: ClienteService,
    private contaService: ContaService,
  ) { }

  ngOnInit(): void {
  }

  buscarContaPorCPf(): void {
    this.contaService.buscarPorCpf(this.cliente.cpf).subscribe({
      next: (data: PesquisaConta) => {
        if (data == null) {
          console.log("Erro ao buscar conta por CPF");
          this.abrirErro();
        } else {
          this.conta = data.conta;
          this.clienteService.buscarPorCpf(this.cliente.cpf).subscribe({
            next: (data: PesquisaCliente) => {
              if (data == null) {
                console.log("Erro ao buscar cliente por CPF");
                this.abrirErro();           
              } else {
                this.cliente = data.cliente;
                this.abrirModal();
              }
            },
            error: (err) => {
              console.error("Erro ao buscar cliente por CPF", err);
              this.abrirErro();
            }
          });
        }
      },
      error: (err) => {
        console.error("Erro ao buscar conta por CPF", err);
        this.abrirErro();
      }
    });
  }

  abrirErro() {
    this.mensagemErro = 'CPF Inválido! Verifique seus dados e tente novamente.';
  }

  abrirModal() {
    if (this.modalCliente) {
      const modal = new bootstrap.Modal(this.modalCliente.nativeElement);
      modal.show();
    } else {
      console.error("Modal element not found");
    }
  }

}
