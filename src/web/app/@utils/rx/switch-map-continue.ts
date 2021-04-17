import {combineLatest, ObservableInput, of, OperatorFunction} from 'rxjs';
import {mergeMap, switchMap} from 'rxjs/operators';

export const switchMapContinue = <T, O>(project: (value: T) => ObservableInput<O>): OperatorFunction<T, [T, O]> =>
  mergeMap((initialValue: T) => combineLatest([
    of(initialValue),
    of(initialValue).pipe(switchMap(project))
  ]));
