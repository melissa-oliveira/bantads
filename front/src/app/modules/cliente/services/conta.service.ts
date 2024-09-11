import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../../auth/services/auth.service';
import { Observable } from 'rxjs';
import { ListaContas, PesquisaConta } from '../../shared';
import { ListaHistoricoMovimentacao } from '../../shared/models/lista-historico-movimentacao';

@Injectable({
  providedIn: 'root'
})
export class ContaService {

  BASE_URL = "http://localhost:3000";

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) { }

  private createHttpOptions() {
    const token = this.authService.getToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token != '' && token != null) {
      headers = headers.append('x-access-token', token);
    }

    return { headers: headers };
  }

  buscarPorCpf(cpf: string): Observable<PesquisaConta> {
    return this.httpClient.get<PesquisaConta>(this.BASE_URL + '/conta/conta/cliente/' + cpf, this.createHttpOptions());
  }

  buscarExtratoPorNumeroConta(numeroConta: string, dataInicio: string, dataFim: string): Observable<ListaHistoricoMovimentacao> {
    return this.httpClient.get<ListaHistoricoMovimentacao>(
      this.BASE_URL + '/conta/extrato/' + numeroConta + "?dataInicio=" + dataInicio + "&dataFim=" + dataFim,
      this.createHttpOptions());
  }
}
