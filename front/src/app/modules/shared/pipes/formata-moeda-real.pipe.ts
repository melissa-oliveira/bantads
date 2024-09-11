import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formataMoedaReal'
})
export class FormataMoedaRealPipe implements PipeTransform {

  transform(value: number, ...args: any[]): string {
    if (typeof value !== 'number') {
      return "R$0,00";
    }

    const options = {
      style: 'currency',
      currency: 'BRL',
      minimumFractionDigits: 2
    };

    return value.toLocaleString('pt-BR', options);
  }

}