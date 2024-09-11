import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ExtratoComponent } from './extrato/extrato.component';
import { PerfilComponent } from './perfil/perfil.component';
import { TelaInicialClienteComponent } from './tela-inicial-cliente/tela-inicial-cliente.component';
import { IConfig, NgxMaskModule } from 'ngx-mask';
import { SharedModule } from '../shared/shared.module';
import { FormsModule } from '@angular/forms';
export const options: Partial<IConfig> | (() => Partial<IConfig>) = {};



@NgModule({
  declarations: [
    ExtratoComponent,
    PerfilComponent,
    TelaInicialClienteComponent,
    TelaInicialClienteComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    NgxMaskModule.forRoot(),
    FormsModule,
    SharedModule
  ]
})
export class ClienteModule { }
