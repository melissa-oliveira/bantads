import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../../auth/services/auth.service';
import { Observable } from 'rxjs';
import { Cliente, PesquisaCliente, PesquisaConta } from '../../shared';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

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

  buscarPorCpf(cpf: string): Observable<PesquisaCliente> {
    return this.httpClient.get<PesquisaCliente>(this.BASE_URL + '/cliente/cliente/cpf/' + cpf, this.createHttpOptions());
  }

  editarPerfil(cliente: Cliente): Observable<PesquisaCliente> {
    return this.httpClient.put<PesquisaCliente>(this.BASE_URL + '/saga/cliente', JSON.stringify(cliente), this.createHttpOptions());
  }

}
