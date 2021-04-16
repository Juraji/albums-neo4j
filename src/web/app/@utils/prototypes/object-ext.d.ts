type NotArray<T, U> = T extends Array<any> ? never : U;

interface Object {
  copy<T>(this: T, update?: NotArray<T, Partial<T>>): T;

  copy<T>(this: T, update: NotArray<T, (original: T) => Partial<T>>): T;
}
