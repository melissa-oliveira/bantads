import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors } from '@angular/forms';

@Directive({
  selector: '[IsMoedaMaiorQueZero]',
  providers: [{
    provide: NG_VALIDATORS,
    useExisting: IsMoedaMaiorQueZeroDirective,
    multi: true
  }]
})
export class IsMoedaMaiorQueZeroDirective {

  validate(control: AbstractControl): ValidationErrors | null {
    const valor = control.value;
    if (valor && valor == 'R$ 0,00') {
      return { isMoedaMaiorQueZero: true };
    }
    return null;
  }

}
