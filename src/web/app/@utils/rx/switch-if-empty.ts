import {EmptyError, Observable, pipe, throwError, UnaryFunction} from 'rxjs';
import {catchError, throwIfEmpty} from 'rxjs/operators';

export const switchIfEmpty = <T>(other: () => Observable<T>): UnaryFunction<Observable<T>, Observable<T>> => pipe(
  throwIfEmpty(),
  catchError(err => err instanceof EmptyError ? other() : throwError(err))
);
