import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'empty'})
export class EmptyPipe implements PipeTransform {

  transform(value: ArrayLike<any> | null | undefined): boolean {
    return !value || value.length === 0;
  }

}
