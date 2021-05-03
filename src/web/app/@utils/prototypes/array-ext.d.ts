interface Array<T> {
  /**
   * Copy this array, replacing the item at {@param index} with {@param replacement}.
   */
  replace(this: Array<T>, index: number, replacement: T): Array<T>;

  /**
   * Removes {@param deleteCount} elements from this array, starting at {@param index} and returns the result.
   */
  removeAt(this: Array<T>, index: number, deleteCount?: number): Array<T>;

  /**
   * True when this array has no elements, false otherwise.
   */
  isEmpty(this: Array<T>): boolean;

  /**
   * Find the first element where {@param predicate} returns true.
   */
  first(this: Array<T>, predicate?: (value: T, index: number, array: T[]) => unknown): T | null;

  /**
   * Find the last element where {@param predicate} returns true.
   */
  last(this: Array<T>, predicate?: (value: T, index: number, array: T[]) => unknown): T | null;

  /**
   * Filter null and undefined elements from this array.
   * Ensures that the TypeScript compiler will not complain about nullable values.
   */
  filterEmpty(this: Array<T>): Array<T extends null | undefined ? never : T>;

  /**
   * Split this array into chunks of {@param chunkSize}
   */
  chunks(this: Array<T>, chunkSize: number): Array<T[]>;

  /**
   * Rotate this array in 2d
   */
  transpose2d<U>(this: Array<T>): T extends U[] ? Array<T> : never;

  /**
   * Uses an internal Set to remove duplicated values from this array.
   */
  unique<K>(this: Array<T>, identity?: (item: T) => K): Array<T>;

  /**
   * Returns a (Fisher-Yates) randomized copy of this array.
   */
  randomize(this: Array<T>): Array<T>;
}
