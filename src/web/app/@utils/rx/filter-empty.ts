import {OperatorFunction, pipe} from 'rxjs';
import {filter, map} from 'rxjs/operators';

export const filterEmpty = <T>(): OperatorFunction<T, NonNullable<T>> => pipe(
  filter(v => !(v === null || v === undefined)) as OperatorFunction<T, NonNullable<T>>,
);
export const filterEmptyArray = <T>(): OperatorFunction<Array<T>, Array<NonNullable<T>>> => pipe(
  map(v => v.filter(e => !(e === null || e === undefined))) as OperatorFunction<Array<T>, Array<NonNullable<T>>>,
);
