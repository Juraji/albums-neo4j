import {Pipe, PipeTransform} from '@angular/core';

const STEP_SIZE = 1024;
const STEP_NAMES: ReadonlyArray<string> = ['bytes', 'KB', 'MB', 'GB', 'TB'];

@Pipe({name: 'fileSize'})
export class FileSizePipe implements PipeTransform {
  transform(bytes?: number, fractionDigits = 1): string {
    if (typeof bytes !== 'number') {
      return `0 ${STEP_NAMES[0]}`;
    }

    let i = 0;
    while (i < STEP_NAMES.length && bytes >= STEP_SIZE) {
      bytes = bytes / STEP_SIZE;
      i++;
    }

    return `${bytes.toFixed(fractionDigits)} ${STEP_NAMES[i]}`;
  }

}
