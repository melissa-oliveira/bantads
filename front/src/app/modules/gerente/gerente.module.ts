import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ListaClientesGerenteComponent } from './lista-clientes-gerente/lista-clientes-gerente.component';
import { MelhoresClientesComponent } from './melhores-clientes/melhores-clientes.component';
import { PesquisarClienteComponent } from './pesquisar-cliente/pesquisar-cliente.component';
import { TelaInicialGerenteComponent } from './tela-inicial-gerente/tela-inicial-gerente.component';
import { NgxMaskModule } from 'ngx-mask';
import { VerClienteComponent } from './ver-cliente/ver-cliente.component';
import { SharedModule } from '../shared/shared.module';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [
    ListaClientesGerenteComponent,
    MelhoresClientesComponent,
    PesquisarClienteComponent,
    TelaInicialGerenteComponent,
    VerClienteComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    NgxMaskModule.forRoot(),
    SharedModule,
    FormsModule
  ]
})
export class GerenteModule { }
