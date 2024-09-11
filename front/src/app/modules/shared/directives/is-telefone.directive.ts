import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[IsTelefone]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: IsTelefoneDirective,
      multi: true,
    },
  ],
})
export class IsTelefoneDirective implements Validator {
  validate(control: AbstractControl): ValidationErrors | null {
    const phonePattern = /^[0-9]{10,11}$/;
    const value = control.value;
    phonePattern.test(value)
    if (!value || phonePattern.test(value)) {
      return null;
    } else {
      return { isTelefone: true };
    }
  }
}
