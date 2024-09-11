import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { TipoUsuario, Usuario } from '../../shared';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
  @ViewChild('loginForm') loginForm!: NgForm;
  usuario!: Usuario;
  mensagemErro: string | null = null;
  senhaVisivel: boolean = false; 

  constructor(
    private authService: AuthService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.usuario = new Usuario();
  }

  public tentarLogin() {
    // if (!this.usuario.senha) {
    //   console.error('Senha não pode estar vazia');
    //   return;
    // }

    if (this.loginForm.form.valid) {
      console.log("Login antes de enviar:", JSON.stringify(this.usuario.email));
      this.authService.login(this.usuario).subscribe({
        next: (res: any) => {
          if (res) {
            this.usuario.id = res.data.id;
            this.usuario.email = res.data.email;
            this.usuario.senha = res.data.senha;
            this.usuario.tipo = res.data.tipo;
            this.usuario.cpf = res.data.cpf;
            this.authService.setToken(res.token);
            this.navbarPorTipoUsuario(this.usuario.tipo);
            console.log(this.usuario.cpf);
            
          } else {
            this.authService.setToken('');
          }
        },
        error: (error: any) => {
          console.error('Erro durante o login:', error);
          if (error.status === 401) {
            this.abrirErro();
          }
        }
      });
    }
  }

  private navbarPorTipoUsuario(userType: TipoUsuario) {
    switch (userType) {
      case TipoUsuario.CLIENTE:
        this.router.navigate(['/cliente']);
        break;
      case TipoUsuario.GERENTE:
        this.router.navigate(['/gerente']);
        break;
      case TipoUsuario.ADMIN:
        this.router.navigate(['/admin']);
        break;
      default:
        console.error('Tipo de usuário desconhecido:', userType);
        break;
    }
  }

  abrirErro() {
    this.mensagemErro = 'Login Inválido! Verifique seus dados e tente novamente.';
  }
}

