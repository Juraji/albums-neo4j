/* eslint-disable */
///<reference path="array-ext.d.ts"/>

export default function () {
  Object.defineProperty(Array.prototype, 'replace', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>, index: number, replacement: T): Array<T> {
      const n = this.slice();
      n[index] = replacement;
      return n;
    },
  });

  Object.defineProperty(Array.prototype, 'isEmpty', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>) {
      return this.length === 0;
    },
  });

  Object.defineProperty(Array.prototype, 'first', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>, predicate: (value: T, index: number, array: T[]) => unknown) {
      return this.filter(predicate).pop() || null;
    },
  });

  Object.defineProperty(Array.prototype, 'filterEmpty', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>) {
      return this.filter(v => !(v === null || v === undefined));
    },
  });
}
