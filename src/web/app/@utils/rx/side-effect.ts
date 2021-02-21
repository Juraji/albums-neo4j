import {MonoTypeOperatorFunction, Observable} from 'rxjs';
import {filter, take, tap} from 'rxjs/operators';

export const sideEffect = <T>(
  action: (value: T) => void,
  predicate?: (value: T) => Observable<boolean>
): MonoTypeOperatorFunction<T> => {
  if (!!predicate) {
    return tap(v => predicate(v)
      .pipe(take(1), filter(b => b))
      .subscribe(() => action(v)));
  } else {
    return tap(action);
  }
};
