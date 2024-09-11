import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Gerente, ListaGerentes, ListaGerentesDashboard, PesquisaGerente, PesquisaUsuario } from '../../shared';
import { AuthService } from '../../auth/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class GerenteService {

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

  listarTodosDashboard(): Observable<ListaGerentesDashboard> {
    return this.httpClient.get<ListaGerentesDashboard>(this.BASE_URL + '/conta/gerente/dashboard', this.createHttpOptions());
  }

  listarTodos(): Observable<ListaGerentes> {
    return this.httpClient.get<ListaGerentes>(this.BASE_URL + '/gerente/gerente', this.createHttpOptions());
  }

  inserir(Gerente: Gerente): Observable<Gerente> {
    return this.httpClient.post<Gerente>(this.BASE_URL + '/saga/gerente', JSON.stringify(Gerente), this.createHttpOptions());
  }

  buscarPorId(id: number): Observable<PesquisaGerente> {
    return this.httpClient.get<PesquisaGerente>(this.BASE_URL + '/gerente/gerente/' + id, this.createHttpOptions());
  }

  buscarUsuarioPorId(email: string): Observable<PesquisaUsuario> {
    return this.httpClient.get<PesquisaUsuario>(this.BASE_URL + '/auth/usuario/' + email, this.createHttpOptions());
  }

  atualizar(Gerente: Gerente): Observable<Gerente> {
    return this.httpClient.put<Gerente>(this.BASE_URL + '/saga/gerente', JSON.stringify(Gerente), this.createHttpOptions());
  }

  remover(cpf: string): Observable<any> {
    return this.httpClient.delete<any>(this.BASE_URL + "/saga/gerente/" + cpf, this.createHttpOptions());
  }

}
