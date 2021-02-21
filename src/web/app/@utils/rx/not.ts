import {MonoTypeOperatorFunction} from 'rxjs';
import {map} from 'rxjs/operators';

export const not = <T>(): MonoTypeOperatorFunction<boolean> => map((v) => !v);
