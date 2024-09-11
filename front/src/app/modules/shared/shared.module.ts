import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IsEmailDirective } from './directives/is-email.directive';
import { IsMoedaRealDirective } from './directives/is-moeda-real.directive';
import { IsNumericoDirective } from './directives/is-numerico.directive';
import { FormataMoedaRealPipe } from './pipes/formata-moeda-real.pipe';
import { IConfig } from 'ngx-mask';
import { IsTelefoneDirective } from './directives/is-telefone.directive';
import { IsCepDirective } from './directives/is-cep.directive';
import { IsCpfDirective } from './directives/is-cpf.directive';
import { IsMoedaMaiorQueZeroDirective } from './directives/is-moeda-maior-que-zero.directive';
export const options: Partial<IConfig> | (() => Partial<IConfig>) = {};



@NgModule({
  declarations: [
    IsEmailDirective,
    IsMoedaRealDirective,
    IsNumericoDirective,
    FormataMoedaRealPipe,
    IsTelefoneDirective,
    IsCepDirective,
    IsCpfDirective,
    IsMoedaMaiorQueZeroDirective
  ],
  exports: [
    IsEmailDirective,
    IsMoedaRealDirective,
    IsNumericoDirective,
    FormataMoedaRealPipe,
    IsTelefoneDirective,
    IsCepDirective,
    IsCpfDirective,
    IsMoedaMaiorQueZeroDirective
  ],
  imports: [
    CommonModule
  ]
})
export class SharedModule { }
