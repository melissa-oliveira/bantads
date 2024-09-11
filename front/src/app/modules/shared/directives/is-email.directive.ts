import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[IsEmail]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: IsEmailDirective,
      multi: true,
    },
  ],
})
export class IsEmailDirective implements Validator {
  validate(control: AbstractControl): ValidationErrors | null {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    const value = control.value;

    if (!value || emailPattern.test(value)) {
      return null;
    } else {
      return { isEmail: true };
    }
  }
}
