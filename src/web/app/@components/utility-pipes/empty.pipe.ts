import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'empty'})
export class EmptyPipe implements PipeTransform {

  transform(value: ArrayLike<any>): boolean {
    return !value || value.length === 0;
  }

}
