import {MonoTypeOperatorFunction} from 'rxjs';
import {tap} from 'rxjs/operators';

export const sideEffect = <T>(
  predicate: (value: T) => boolean,
  action: (value: T) => void
): MonoTypeOperatorFunction<T> => tap(v => {
  if (predicate(v)) {
    action(v);
  }
});
