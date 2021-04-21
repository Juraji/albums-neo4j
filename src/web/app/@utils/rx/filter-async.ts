import {MonoTypeOperatorFunction, Observable} from 'rxjs';
import {filter, mapTo, mergeMap} from 'rxjs/operators';
import {once} from './once';

export const filterAsync = <T>(
  predicate: (value: T) => Observable<boolean>
): MonoTypeOperatorFunction<T> => mergeMap(v => predicate(v)
  .pipe(once(), filter(b => b), mapTo(v)));

