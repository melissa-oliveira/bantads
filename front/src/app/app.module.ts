import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AuthModule } from './modules/auth/auth.module';
import { ClienteModule } from './modules/cliente/cliente.module';
import { GerenteModule } from './modules/gerente/gerente.module';
import { AdminModule } from './modules/admin/admin.module';
import { ErrorsModule } from './modules/errors/errors.module';
import { NgxMaskModule } from 'ngx-mask';
import { SharedModule } from './modules/shared/shared.module';

@NgModule({
  declarations: [
    AppComponent
  ],  
  imports: [
    BrowserModule,
    AppRoutingModule,
    CommonModule,
    FormsModule,
    HttpClientModule,
    AuthModule,
    ClienteModule,
    GerenteModule,
    AdminModule,
    ErrorsModule,
    SharedModule,
    NgxMaskModule.forRoot(),
    SharedModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
