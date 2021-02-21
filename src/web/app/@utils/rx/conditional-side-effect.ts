import {MonoTypeOperatorFunction, Observable} from 'rxjs';
import {filter, take, tap} from 'rxjs/operators';

export const sideEffect = <T>(action: (value: T) => void): MonoTypeOperatorFunction<T> => tap(action);

export const conditionalSideEffect = <T>(
  predicate: (value: T) => Observable<boolean>,
  action: (value: T) => void
): MonoTypeOperatorFunction<T> => tap((v) => predicate(v)
  .pipe(
    take(1),
    filter(b => b),
  ).subscribe(() => action(v)));
