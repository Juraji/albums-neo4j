import {OperatorFunction} from 'rxjs';
import {map} from 'rxjs/operators';

export const not = <T>(): OperatorFunction<T, boolean> => map((v) => !v);
