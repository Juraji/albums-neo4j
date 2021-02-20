import {MonoTypeOperatorFunction, Observable} from 'rxjs';
import {filter, mapTo, mergeMap} from 'rxjs/operators';

export const filterAsync = <T>(
  predicate: (value: T, index: number) => Observable<boolean>
): MonoTypeOperatorFunction<T> => mergeMap((v, i) => predicate(v, i)
  .pipe(
    filter(b => b),
    mapTo(v)
  ));

