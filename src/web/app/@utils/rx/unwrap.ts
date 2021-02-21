import {map} from 'rxjs/operators';
import {OperatorFunction} from 'rxjs';

export const unwrap =
  <T, K extends keyof T, R extends T[K]>(key: K): OperatorFunction<T, R> =>
    map((v) => v[key] as R);
