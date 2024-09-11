import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PerfilComponent } from './modules/cliente/perfil/perfil.component';
import { ExtratoComponent } from './modules/cliente/extrato/extrato.component';
import { TelaInicialGerenteComponent } from './modules/gerente/tela-inicial-gerente/tela-inicial-gerente.component';
import { TelaInicialClienteComponent } from './modules/cliente/tela-inicial-cliente/tela-inicial-cliente.component';
import { ListaClientesGerenteComponent } from './modules/gerente/lista-clientes-gerente/lista-clientes-gerente.component';
import { PesquisarClienteComponent } from './modules/gerente/pesquisar-cliente/pesquisar-cliente.component';
import { MelhoresClientesComponent } from './modules/gerente/melhores-clientes/melhores-clientes.component';
import { TelaInicialAdminComponent } from './modules/admin/tela-inicial-admin/tela-inicial-admin.component';
import { ListaClientesAdminComponent } from './modules/admin/lista-clientes-admin/lista-clientes-admin.component';
import { ListaGerentesAdminComponent } from './modules/admin/lista-gerentes-admin/lista-gerentes-admin.component';
import { InserirGerentesAdminComponent } from './modules/admin/inserir-gerentes-admin/inserir-gerentes-admin.component';
import { EditarGerenteComponent } from './modules/admin/editar-gerente/editar-gerente.component';
import { LoginComponent } from './modules/auth/login/login.component';
import { AutocadastroComponent } from './modules/auth/autocadastro/autocadastro.component';
import { PageNotFoundComponent } from './modules/errors/page-not-found/page-not-found.component';
import { AuthGuard } from './modules/auth/auth.guard';
import { UnauthorizedComponent } from './modules/errors/unauthorized/unauthorized.component';
import { VerClienteComponent } from './modules/gerente/ver-cliente/ver-cliente.component';

const routes: Routes = [
  // Login
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },

  // Autocadastro
  { path: '', redirectTo: '/autocadastro', pathMatch: 'full' },
  { path: 'autocadastro', component: AutocadastroComponent },

  // Cliente
  { path: 'cliente/perfil', component: PerfilComponent, canActivate: [AuthGuard], data: { role: 'CLIENTE' } },
  { path: 'cliente/extrato', component: ExtratoComponent, canActivate: [AuthGuard], data: { role: 'CLIENTE' } },
  { path: 'cliente', component: TelaInicialClienteComponent, canActivate: [AuthGuard], data: { role: 'CLIENTE' } },

  // Gerente
  { path: 'gerente', component: TelaInicialGerenteComponent, canActivate: [AuthGuard], data: { role: 'GERENTE' } },
  { path: 'gerente/clientes', component: ListaClientesGerenteComponent, canActivate: [AuthGuard], data: { role: 'GERENTE' } },
  { path: 'gerente/pesquisar-cliente', component: PesquisarClienteComponent, canActivate: [AuthGuard], data: { role: 'GERENTE' } },
  { path: 'gerente/melhores-clientes', component: MelhoresClientesComponent, canActivate: [AuthGuard], data: { role: 'GERENTE' } },
  { path: 'gerente/cliente/:cpf', component: VerClienteComponent, canActivate: [AuthGuard], data: { role: 'GERENTE' } },

  // Adiministrador
  { path: 'admin', component: TelaInicialAdminComponent, canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/clientes', component: ListaClientesAdminComponent, canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/gerentes', component: ListaGerentesAdminComponent, canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/gerentes/novo', component: InserirGerentesAdminComponent, canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'admin/gerentes/:id', component: EditarGerenteComponent, canActivate: [AuthGuard], data: { role: 'ADMIN' } },

  // Erros
  { path: 'nao-autorizado', component: UnauthorizedComponent },

  // Not Found
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
