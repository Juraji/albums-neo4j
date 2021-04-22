import {Observable, of, pipe, UnaryFunction} from 'rxjs';
import {bufferTime, mergeMap} from 'rxjs/operators';

export const distinctOverTime = <T>(timeSpan: number, identity?: (t: T) => any):
  UnaryFunction<Observable<T>, Observable<T>> => pipe(
  bufferTime<T>(timeSpan),
  mergeMap(buf => of(...buf.unique(identity))),
);
