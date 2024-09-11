import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EditarGerenteComponent } from './editar-gerente/editar-gerente.component';
import { InserirGerentesAdminComponent } from './inserir-gerentes-admin/inserir-gerentes-admin.component';
import { ListaClientesAdminComponent } from './lista-clientes-admin/lista-clientes-admin.component';
import { TelaInicialAdminComponent } from './tela-inicial-admin/tela-inicial-admin.component';
import { ListaGerentesAdminComponent } from './lista-gerentes-admin/lista-gerentes-admin.component';
import { NgxMaskModule } from 'ngx-mask';
import { SharedModule } from '../shared/shared.module';



@NgModule({
  declarations: [
    EditarGerenteComponent,
    InserirGerentesAdminComponent,
    ListaClientesAdminComponent,
    ListaGerentesAdminComponent,
    TelaInicialAdminComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    NgxMaskModule.forRoot(),
    SharedModule
  ]
})
export class AdminModule { }
