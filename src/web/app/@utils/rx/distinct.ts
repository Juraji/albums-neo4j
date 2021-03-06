import {MonoTypeOperatorFunction, pipe} from 'rxjs';
import {distinctUntilChanged} from 'rxjs/operators';

export const distinctUntilChangedArrayExact = <T extends any[]>(): MonoTypeOperatorFunction<T> => pipe(
  distinctUntilChanged((x, y) => !x.some((v, i) => v !== y[i])));
