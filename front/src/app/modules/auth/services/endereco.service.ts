import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Endereco } from 'src/app//modules/shared/models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class EnderecoService {

  BASE_URL = "http://localhost:8081/enderecos";
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private httpClient: HttpClient) { }

  inserir(endereco: Endereco): Observable<Endereco> {
    return this.httpClient.post<Endereco>(this.BASE_URL, JSON.stringify(endereco), this.httpOptions);
  }
}
