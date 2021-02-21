import {
  AbstractControl,
  AbstractControlOptions,
  AsyncValidatorFn,
  FormGroup as NgFormGroup,
  ValidatorFn
} from '@angular/forms';
import {Observable} from 'rxjs';

type FormControls<T> = Record<keyof T, AbstractControl>;

interface SetOptions {
  onlySelf?: boolean;
  emitEvent?: boolean;
}

interface TypedControl<T> {
  value: T;
  valueChanges: Observable<T>;
}

export class FormGroup<T = any> extends NgFormGroup implements TypedControl<T> {

  constructor(
    controls: FormControls<T>,
    validatorOrOpts?: ValidatorFn | ValidatorFn[] | AbstractControlOptions | null,
    asyncValidator?: AsyncValidatorFn | AsyncValidatorFn[] | null) {
    super(controls, validatorOrOpts, asyncValidator);
  }

  setValue(value: Partial<T>, options?: SetOptions) {
    super.setValue(value, options);
  }

  reset(value?: Partial<T>, options?: SetOptions) {
    super.reset(value, options);
  }
}
