import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    // Verifica se existe um usuário logado
    if (this.authService.isUsuarioLogado()) {

      const TipoUsuario = this.authService.getTipoFromToken();
      const papelNecessario = route.data?.['role'];

      // Verifica se o usuário logado tem o papel necessário para acessar a página
      if (TipoUsuario == papelNecessario) {
        return true;
      }
      // Se o usuário logado NÃO tem o papel necessário para acessar a página
      else {
        this.router.navigate(['/nao-autorizado']);
        return false;
      }
    }

    // Se não existe usuário logado
    else {
      this.router.navigate(['/nao-autorizado']);
      return false;
    }
  }
}
