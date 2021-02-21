import {OperatorFunction, pipe} from 'rxjs';
import {filter} from 'rxjs/operators';

export const filterEmpty = <T>(): OperatorFunction<T, T extends null | undefined ? never : T> => pipe(
  filter(v => !(v === null || v === undefined)) as OperatorFunction<T, T extends null | undefined ? never : T>,
);
