import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './modules/auth/services/auth.service';
import { TipoUsuario, Usuario } from './modules/shared';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'BANTADS';
  cliente: TipoUsuario = TipoUsuario.CLIENTE;
  gerente: TipoUsuario = TipoUsuario.GERENTE;
  admin: TipoUsuario = TipoUsuario.ADMIN;

  constructor(
    private router: Router,
    private authService: AuthService) { }

    usuarioLogado(): Usuario {
      const usuarioLogado = new Usuario();
  
      const tipoUsuario = this.authService.getTipoFromToken();
  
      if (tipoUsuario === "CLIENTE") {
        usuarioLogado.tipo = TipoUsuario.CLIENTE;
      } else if (tipoUsuario === "GERENTE") {
        usuarioLogado.tipo = TipoUsuario.GERENTE;
      } else if (tipoUsuario === "ADMIN") {
        usuarioLogado.tipo = TipoUsuario.ADMIN;
      }
  
      return usuarioLogado;
    }

  qualLink(): string {
    return this.router.url;
  }

  logout() {
    this.authService.logout();
    this.authService.setToken('');
    this.router.navigate(['/login']);
  }
}
