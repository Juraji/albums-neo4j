import {MonoTypeOperatorFunction, Observable} from 'rxjs';
import {filter, mapTo, mergeMap, take} from 'rxjs/operators';

export const filterAsync = <T>(
  predicate: (value: T) => Observable<boolean>
): MonoTypeOperatorFunction<T> => mergeMap(v => predicate(v)
  .pipe(take(1), filter(b => b), mapTo(v)));

