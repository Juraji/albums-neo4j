/* eslint-disable */
///<reference path="object-ext.d.ts"/>

export default function () {
  Object.defineProperty(Object.prototype, 'copy', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: T, update?: Partial<T> | ((t: T) => Partial<T>)): T {
      if (Array.isArray(this)) {
        if (!!update) {
          throw Error('Can not apply update to arrays');
        }

        // @ts-ignore
        return this.slice();
      } else if (typeof update === "function") {
        return Object.assign({}, this, update(this));
      } else {
        return Object.assign({}, this, update);
      }
    },
  });
}
