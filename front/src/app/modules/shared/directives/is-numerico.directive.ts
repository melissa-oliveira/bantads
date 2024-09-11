import { Directive, ElementRef, HostListener } from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';

@Directive({
  selector: '[appIsNumerico]'
})

export class IsNumericoDirective implements ControlValueAccessor {

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
    if (!value) value = "";
    this.el.nativeElement.value = value;
  }

  @HostListener('keyup', ['$event'])
  onKeyUp($event: any) {
    let valor = $event.target.value;
    valor = valor.replace(/[\D]/g, '');
    $event.target.value = valor;
    this.onChange(valor);
  }
}