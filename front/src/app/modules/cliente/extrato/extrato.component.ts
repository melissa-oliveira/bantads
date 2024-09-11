import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Conta, ListaContas, PesquisaConta } from '../../shared';
import { ContaService } from '../services/conta.service';
import { AuthService } from '../../auth/services/auth.service';
import { HistoricoMovimentacao } from '../../shared/models/historico-movimentacao';
import { ListaHistoricoMovimentacao } from '../../shared/models/lista-historico-movimentacao';

type FiltroData = {
  dataInicio: string,
  dataFim: string,
}

@Component({
  selector: 'app-extrato',
  templateUrl: './extrato.component.html',
  styleUrls: ['./extrato.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ExtratoComponent implements OnInit {
  @ViewChild('formFiltro') formFiltro!: NgForm;

  conta: Conta = new Conta();
  historico: HistoricoMovimentacao[] = [];
  cpf: string = '';
  filtro: FiltroData = { dataInicio: '', dataFim: '' };
  tabelaHTML: string = '';

  constructor(
    private contaService: ContaService,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    this.cpf = this.authService.getCpfFromToken();
    this.buscarContaPorCPf();
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

  buscarExtratoPorNumeroConta() {
    const dataInicio = this.filtro.dataInicio + " 00:00:00";
    const dataFim = this.filtro.dataFim + " 23:59:59";

    this.contaService.buscarExtratoPorNumeroConta(this.conta.numeroConta, dataInicio, dataFim).subscribe({
      next: (data: ListaHistoricoMovimentacao) => {
        if (data == null) {
          console.log("Extrato vazio");
          this.historico = [];
          this.tabelaHTML = this.gerarHistoricoHTML(this.historico);
        } else {
          this.historico = data.historicos;
          console.log(this.historico);
          this.tabelaHTML = this.gerarHistoricoHTML(this.historico);
        }
      }
    });
  }

  gerarHistoricoHTML(historico: HistoricoMovimentacao[]): string {
    let saldo = 0;
    let tabelaHTML = `<table class="btds-shadow-50 btds-borda-1">
      <thead>
        <tr>
          <th>Data</th>
          <th>Origem / Destino</th>
          <th>Operação</th>
          <th>Valor</th>
        </tr>
      </thead>
      <tbody>`;

    let dataAtual = '';

    historico.forEach(movimentacao => {
      const [dataMov] = movimentacao.dataHora.split(' ');

      // Se a data mudou, adiciona uma nova linha de saldo do dia
      if (dataMov !== dataAtual) {
        if (dataAtual !== '') {
          const dataFormatada = this.formatarDataHora(`${dataAtual} 00:00:00`).split(' ')[0]; // Formatação da data do totalizador
          tabelaHTML += `<tr class="dia">
            <td>${dataFormatada}</td>
            <td></td>
            <td></td>
            <td><span class="fw-400 bg-off">Saldo:&nbsp; </span> R$ ${saldo.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</td>
          </tr>`;
        }
        dataAtual = dataMov;
      }

      let classe = '';
      let destino = '-';

      if (movimentacao.tipo === 'DEPOSITO') {
        classe = 'entrada';
        saldo += movimentacao.valor;
      } else if (movimentacao.tipo === 'SAQUE') {
        classe = 'saida';
        saldo -= movimentacao.valor;
      } else if (movimentacao.tipo === 'TRANSFERENCIA') {
        if (movimentacao.clienteOrigem?.cpf === this.cpf) { // Se eu sou o remetente
          classe = 'saida';
          saldo -= movimentacao.valor;
          destino = movimentacao.clienteDestino ? movimentacao.clienteDestino.nome : '-';
        } else if (movimentacao.clienteDestino?.cpf === this.cpf) { // Se eu sou o destinatário
          classe = 'entrada';
          saldo += movimentacao.valor;
          destino = movimentacao.clienteOrigem ? movimentacao.clienteOrigem.nome : '-';
        }
      }

      const operacao = movimentacao.tipo.charAt(0) + movimentacao.tipo.slice(1).toLowerCase(); // Capitaliza
      const dataHoraFormatada = this.formatarDataHora(movimentacao.dataHora); // Formatação da data e hora
      tabelaHTML += `<tr class="${classe}">
        <td>${dataHoraFormatada}</td>
        <td>${destino}</td>
        <td>${operacao}</td>
        <td>R$ ${movimentacao.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</td>
      </tr>`;
    });

    // Adiciona a última linha de saldo do dia
    if (dataAtual !== '') {
      const dataFormatada = this.formatarDataHora(`${dataAtual} 00:00:00`).split(' ')[0]; // Formatação da última data
      tabelaHTML += `<tr class="dia">
        <td>${dataFormatada}</td>
        <td></td>
        <td></td>
        <td><span class="fw-400 bg-off">Saldo:&nbsp; </span> R$ ${saldo.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</td>
      </tr>`;
    }

    tabelaHTML += `</tbody></table>`;
    return tabelaHTML;
  }

  formatarDataHora(dataHora: string): string {
    const [data, hora] = dataHora.split(' ');
    const [ano, mes, dia] = data.split('-');
    return `${dia}/${mes}/${ano} ${hora}`;
  }

}
