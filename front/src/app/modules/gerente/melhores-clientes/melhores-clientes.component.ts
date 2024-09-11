import { Component, OnInit } from '@angular/core';
import { ContaService } from '../services/conta.service';
import { AuthService } from '../../auth/services/auth.service';
import { Conta, ListaContas } from '../../shared';

@Component({
  selector: 'app-melhores-clientes',
  templateUrl: './melhores-clientes.component.html',
  styleUrls: ['./melhores-clientes.component.css']
})
export class MelhoresClientesComponent implements OnInit{ 

  contasFiltradas: Conta[] = [];
  cpfGerente: string = '';
  contas: Conta[] = [];


  constructor(
    private contaService: ContaService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.cpfGerente = this.authService.getCpfFromToken();
    this.listarMelhoresClientes();
  }

  listarMelhoresClientes(): void {
    this.contaService.listarMelhoresClientes(this.cpfGerente).subscribe({
      next: (data: ListaContas) => {
        if (data == null) {
          this.contas = [];
          this.contasFiltradas = [];
        } else {
          this.contas = data.contas;
          this.contasFiltradas = data.contas;
        }
      }
    });
  }

  getClienteNome(conta: Conta): string {
    return conta?.cliente?.nome ?? '';
  }

  getClienteCpf(conta: Conta): string {
    return conta?.cliente?.cpf ?? '';
  }

  getLimite(conta: Conta): number {
    return conta?.limite ?? 0;
  }

  getGerenteNome(conta: Conta): string {
    return conta?.gerente?.nome ?? '';
  }

  getSaldo(conta: Conta): number {
    return conta?.saldo ?? 0;
  }
}
