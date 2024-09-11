import { Directive, ElementRef, HostListener } from '@angular/core';
import { AbstractControl, ControlValueAccessor, NG_VALUE_ACCESSOR, ValidationErrors } from '@angular/forms';

@Directive({
  selector: '[IsMoedaReal]',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: IsMoedaRealDirective,
      multi: true,
    },
  ]
})
export class IsMoedaRealDirective implements ControlValueAccessor {

  onChange: any;
  onTouched: any;

  constructor(private el: ElementRef) { }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(value: any): void {
    if (value === "" || value === null || value === undefined) {
      value = 0;
    }

    const formattedValue = this.formatValor(value);
    this.el.nativeElement.value = formattedValue;
  }

  private formatValor(valor: string): string {
    if (!(typeof valor === 'string')) {
      valor = valor + '00';
      valor = valor.toString();
    }
    if (valor === '' || valor === '0') {
      return 'R$ 0,00';
    }
    if (valor.length <= 2) {
      valor = '0,' + valor;
    } else {
      valor = valor.replace(/(\d)(\d{2})$/, '$1,$2');
    }
    valor = valor.replace(/^0+(?=\d)/, '');
    valor = valor.replace(/(?=(\d{3})+(\D))\B/g, '.');
    valor = 'R$ ' + valor;
    return valor;
  }

  @HostListener('keyup', ['$event'])
  onKeyUp($event: any) {
    let valor = $event.target.value;
    valor = valor.replace(/[^\d]/g, '');
    const formattedValue = this.formatValor(valor);
    $event.target.value = formattedValue;
    this.onChange(formattedValue);
  }
}
