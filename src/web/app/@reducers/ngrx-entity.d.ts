/**
 * Make some of the @ngrx/entity interfaces ambient for type declarations.
 * These can be mixed with the original interfaces, as the compiler will merge the definitions
 */
declare interface EntityState<T> {
  ids: string[] | number[];
  entities: Record<string | number, T | undefined>;
}
