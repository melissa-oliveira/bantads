import { Component, OnInit, ViewChild } from '@angular/core';
import { Conta, PesquisaConta, TipoTransacao, Usuario } from '../../shared';
import { ClienteService } from '../services/cliente.service';
import { AuthService } from '../../auth/services/auth.service';
import { Transacao } from '../../shared/models/transacao';
import { NgForm } from '@angular/forms';
import { ContaService } from '../services/conta.service';

@Component({
  selector: 'app-tela-inicial-cliente',
  templateUrl: './tela-inicial-cliente.component.html',
  styleUrls: ['./tela-inicial-cliente.component.css']
})
export class TelaInicialClienteComponent implements OnInit {

  @ViewChild('formDeposito') formDeposito!: NgForm;
  @ViewChild('formSaque') formSaque!: NgForm;
  @ViewChild('formTransacao') formTransacao!: NgForm;

  conta: Conta = new Conta();
  deposito: Transacao = new Transacao();
  saque: Transacao = new Transacao();
  transferencia: Transacao = new Transacao();
  cpf: string = '';
  mensagemErro: string | null = null;

  constructor(
    private contaService: ContaService,
    private clienteService: ClienteService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.cpf = this.authService.getCpfFromToken();
    this.buscarPorCPf();
    this.deposito = new Transacao();
  }

  converterParaFloat(valorEmString: string): number {
    console.log("Valor original:", valorEmString);
  
    let valorFormatado = valorEmString.replace("R$", "");
  
    valorFormatado = valorFormatado.replace(/\s/g, "");
  
    valorFormatado = valorFormatado.replace(/\./g, "");
  
    valorFormatado = valorFormatado.replace(",", ".");;
  
    const valorFloat = parseFloat(valorFormatado);
    console.log("Valor final em float:", valorFloat);
  
    return valorFloat;
  }
  buscarPorCPf(): void {
    this.contaService.buscarPorCpf(this.cpf).subscribe({
      next: (data: PesquisaConta) => {
        if (data == null) {
          // handle null case
        } else {
          this.conta = data.conta;
          console.log(this.conta);
        }
      }
    });
  }

  fazerTransacao(transacao: Transacao): void {
    this.clienteService.fazerTransacao(transacao).subscribe({
      next: (data: Transacao) => {
        if (data == null) {
          console.log("Erro na transação.");
        } else {
          transacao = data;
          this.buscarPorCPf();
        }
      },
      error: (error: any) => {
        if (error.status === 400) {
          this.abrirErro();
        }
      }
    });
  }

  fazerDeposito(): void {
    this.deposito.tipo = TipoTransacao.DEPOSITO;
    this.deposito.contaOrigem = this.conta.numeroConta;
    let valorEmString: string = this.deposito.valor.toString();
    this.deposito.valor = this.converterParaFloat(valorEmString);
    this.fazerTransacao(this.deposito);
  }

  fazerSaque(): void {
    this.saque.tipo = TipoTransacao.SAQUE;
    this.saque.contaOrigem = this.conta.numeroConta;
    let valorEmString: string = this.saque.valor.toString();
    this.saque.valor = this.converterParaFloat(valorEmString);
    this.fazerTransacao(this.saque);

  }

  fazerTansferencia(): void {
    this.transferencia.tipo = TipoTransacao.TRANSFERENCIA;
    this.transferencia.contaOrigem = this.conta.numeroConta;
    let valorEmString: string = this.transferencia.valor.toString();
    this.transferencia.valor = this.converterParaFloat(valorEmString);
    this.fazerTransacao(this.transferencia);
  }

  abrirErro() {
    this.mensagemErro = 'O valor é maior que o seu saldo e o seu limite. Tente novamente com um valor mais baixo!';
  }
}
