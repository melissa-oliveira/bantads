import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente, Conta, ListaContas } from '../../shared';
import { AuthService } from '../../auth/services/auth.service';

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

  listarTodosAprovados(): Observable<ListaContas> {
    return this.httpClient.get<ListaContas>(this.BASE_URL + '/conta/conta/aprovado', this.createHttpOptions());
  }
}
