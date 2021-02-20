interface Array<T> {
  replace(this: Array<T>, index: number, replacement: T): Array<T>;

  withinBounds(this: Array<T>, index: number, startExclusive?: boolean, endExclusive?: boolean): boolean;

  isAtEnd(this: Array<T>, index: number): boolean;

  isEmpty(this: Array<T>): boolean;

  first(this: Array<T>, predicate: (value: T, index: number, array: T[]) => unknown): T | null;
}
