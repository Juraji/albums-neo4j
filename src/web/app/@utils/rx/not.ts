import {OperatorFunction} from 'rxjs';
import {map} from 'rxjs/operators';

export const not = <T>(stateSelector: (value: T) => boolean = v => !v): OperatorFunction<T, boolean> => map(stateSelector);
