// eslint-disable-next-line @typescript-eslint/naming-convention
export const ObserveProperty = (
  sourcePropertyKey: string | symbol
): PropertyDecorator => (
  target: any,
  propertyKey: string | symbol
) => {
  const internalKey = Symbol(`__${sourcePropertyKey.toString()}`);
  Object.defineProperties(target, {
    [sourcePropertyKey]: {
      get() {
        return this[internalKey];
      },
      set(value) {
        this[internalKey] = value;
        if(value !== null && value !== undefined){
          this[propertyKey].next(value);
        }
      }
    }
  });
};
