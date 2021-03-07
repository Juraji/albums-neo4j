import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'notEmpty'})
export class NotEmptyPipe implements PipeTransform {

  transform(value: ArrayLike<any> | null | undefined): boolean {
    return !!value && value.length !== 0;
  }

}
