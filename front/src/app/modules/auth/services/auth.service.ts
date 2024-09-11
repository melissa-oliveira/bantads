import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TipoUsuario, Usuario } from '../../shared';
import { jwtDecode } from "jwt-decode";

interface DecodedToken {
  cpf: string;
  tipo: string;
}

const LS_CHAVE: string = "usuarioLogado";
const CPF: string = "cpf";
const TOKEN: string = "";
const TIPO_USUARIO: TipoUsuario = TipoUsuario.CLIENTE;

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  BASE_URL = "http://localhost:3000";
  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private httpClient: HttpClient
  ) { }

  // Manipulação Local
  usuarioLogado: Usuario = new Usuario();


  public isUsuarioLogado(): boolean {
    if (this.getToken() != '') return true;
    else return false;
  }

  public setToken(token: string) {
    localStorage[TOKEN] = token;
  }

  public getToken() {
    return localStorage[TOKEN];
  }

  private decodeToken(): DecodedToken{
    const token = this.getToken();
      return jwtDecode<DecodedToken>(token);
  }

  // Função para pegar o CPF do token
  getCpfFromToken(): string {
    const decoded = this.decodeToken();
    return decoded.cpf;
  }

  // Função para pegar o Tipo do token
  getTipoFromToken(): string {
    const decoded = this.decodeToken();
    return decoded.tipo;
  }

  // Comunicação com API Gateway
  login(usuario: Usuario): Observable<Usuario> {
    return this.httpClient.post<Usuario>(this.BASE_URL + '/auth/login', JSON.stringify(usuario), this.httpOptions);
  }

  logout() {
    delete localStorage[TOKEN];
  }

  
}
