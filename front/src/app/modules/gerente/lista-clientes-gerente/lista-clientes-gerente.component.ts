import { Component, OnInit } from '@angular/core';
import { ContaService } from '../services/conta.service';
import { Conta, ListaContas } from '../../shared';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-lista-clientes-gerente',
  templateUrl: './lista-clientes-gerente.component.html',
  styleUrls: ['./lista-clientes-gerente.component.css']
})
export class ListaClientesGerenteComponent implements OnInit {

  contas: Conta[] = [];
  contasFiltradas: Conta[] = [];
  cpfGerente: string = '';
  nomeFiltro: string = '';
  cpfFiltro: string = '';

  constructor(
    private contaService: ContaService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.cpfGerente = this.authService.getCpfFromToken();
    this.listarContasAprovadas();
  }

  listarContasAprovadas(): void {
    this.contaService.listarTodosAprovados(this.cpfGerente).subscribe({
      next: (data: ListaContas) => {
        if (data == null) {
          this.contas = [];
          this.contasFiltradas = [];
        } else {
          const contasOrdenadas = data.contas.sort((a, b) => {
            if (a.cliente.nome < b.cliente.nome) {
              return -1;
            }
            if (a.cliente.nome > b.cliente.nome) {
              return 1;
            }
            return 0;
          });
  
          this.contas = contasOrdenadas;
          this.contasFiltradas = contasOrdenadas;
        }
      }
    });
  }
  

  filterContas(): void {
    const nomeFiltroLower = this.nomeFiltro.toLowerCase();
    const cpfFiltroSanitized = this.cpfFiltro.replace(/\D/g, '');

    this.contasFiltradas = this.contas.filter(conta => {
      const nomeMatches = conta.cliente.nome.toLowerCase().includes(nomeFiltroLower);
      const cpfMatches = conta.cliente.cpf.replace(/\D/g, '').includes(cpfFiltroSanitized);
      return nomeMatches && cpfMatches;
    });
  }
}
