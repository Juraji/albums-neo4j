// @ts-nocheck
import {MonoTypeOperatorFunction, Observable, Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {OnDestroy} from '@angular/core';

const untilDestroyedSymbol = Symbol('__untilDestroyed__');

export function untilDestroyed<T extends OnDestroy, U>(
  instance: T
): MonoTypeOperatorFunction<U> {
  return (source: Observable<U>) => {
    const originalDestroy = instance.ngOnDestroy;
    const hasDestroyFunction = typeof originalDestroy === 'function';

    if (!hasDestroyFunction) {
      throw new Error(
        `${instance.constructor.name} is using untilDestroyed but doesn't implement OnDestroy`
      );
    }

    if (!instance[untilDestroyedSymbol]) {
      instance[untilDestroyedSymbol] = new Subject();

      instance.ngOnDestroy = function (): void {
        if (hasDestroyFunction) {
          originalDestroy.apply(this, arguments);
        }

        if (!!instance[untilDestroyedSymbol]) {
          instance[untilDestroyedSymbol].next();
          instance[untilDestroyedSymbol].complete();
          instance[untilDestroyedSymbol] = null;
        }
      };
    }

    return source.pipe(takeUntil<U>(instance[untilDestroyedSymbol]));
  };
}
