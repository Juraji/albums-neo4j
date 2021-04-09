/* eslint-disable */
///<reference path="array-ext.d.ts"/>

export default function () {
  Object.defineProperty(Array.prototype, 'append', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>, ...items: Array<T>): Array<T> {
      return [...this, ...items];
    },
  });

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

  Object.defineProperty(Array.prototype, 'chunks', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>, chunkSize: number) {
      return Array.from({length: Math.ceil(this.length / chunkSize)}, (_, i) =>
        this.slice(i * chunkSize, i * chunkSize + chunkSize)
      );
    },
  });

  Object.defineProperty(Array.prototype, 'transpose2d', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T, U extends T[]>(this: Array<U>) {
      if (this.length === 0) {
        return this;
      } else {
        const padWidth = Math.max(...this.map(r => r.length));
        const normalizedInputs = this.map(r => r.concat(new Array(padWidth)).slice(0, padWidth))
        return (Array.from({length: padWidth}))
          .map((row, i) => normalizedInputs.map(col => col[i]).filterEmpty());
      }
    },
  });

  Object.defineProperty(Array.prototype, 'unique', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>) {
      const uqSet = this.reduce((acc, next) => acc.add(next), new Set<T>());
      return Array.from(uqSet.values());
    },
  });
}
