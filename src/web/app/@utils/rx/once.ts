import {take} from 'rxjs/operators';
import {MonoTypeOperatorFunction} from 'rxjs';

export const once = <T>(): MonoTypeOperatorFunction<T> => take(1);
