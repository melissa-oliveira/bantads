import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Cliente } from 'src/app/modules/shared';

@Injectable({
  providedIn: 'root'
})

export class AutocadastroService {

  BASE_URL = "http://localhost:3000";
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private httpClient: HttpClient) { }

  inserir(Cliente: Cliente): Observable<Cliente> {
    return this.httpClient.post<Cliente>(this.BASE_URL + '/saga/autocadastro', JSON.stringify(Cliente), this.httpOptions);
  }
}
