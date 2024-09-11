import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors } from '@angular/forms';

@Directive({
  selector: '[IsCep]',
  providers: [
    {
      provide: NG_VALIDATORS,
      useExisting: IsCepDirective,
      multi: true,
    },
  ],
})
export class IsCepDirective {

  validate(control: AbstractControl): ValidationErrors | null {
    const phonePattern = /^[0-9]{8}$/;
    const value = control.value;
    phonePattern.test(value)
    if (!value || phonePattern.test(value)) {
      return null;
    } else {
      return { isCep: true };
    }
  }

}
