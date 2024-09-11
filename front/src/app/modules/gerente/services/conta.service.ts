import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../../auth/services/auth.service';
import { Observable } from 'rxjs';
import { AnaliseConta, ListaContas, ListaCpfs, PesquisaConta, RespostaAnaliseConta } from '../../shared';

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

  listarTodosCpfsAnalise(cpf: string): Observable<ListaCpfs> {
    return this.httpClient.get<ListaCpfs>(this.BASE_URL + '/conta/conta/analise/cpf/gerente/' + cpf, this.createHttpOptions());
  }

  listarTodosAprovados(cpf: string): Observable<ListaContas> {
    return this.httpClient.get<ListaContas>(this.BASE_URL + '/conta/conta/aprovado/gerente/' + cpf, this.createHttpOptions());
  }

  aprovarConta(analiseConta: AnaliseConta): Observable<RespostaAnaliseConta> {
    return this.httpClient.post<RespostaAnaliseConta>(this.BASE_URL + '/conta/conta/aprovar', JSON.stringify(analiseConta), this.createHttpOptions());
  }

  recusarConta(analiseConta: AnaliseConta): Observable<RespostaAnaliseConta> {
    return this.httpClient.post<RespostaAnaliseConta>(this.BASE_URL + '/conta/conta/recusar', JSON.stringify(analiseConta), this.createHttpOptions());
  }

  listarMelhoresClientes(cpf: string): Observable<ListaContas> {
    return this.httpClient.get<ListaContas>(this.BASE_URL + '/conta/conta/melhoresClientes/' + cpf, this.createHttpOptions());
  }
}
