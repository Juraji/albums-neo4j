import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'fileSize'
})
export class FileSizePipe implements PipeTransform {
  private static readonly dictionary: ReadonlyArray<string> = ['bytes', 'KB', 'MB', 'GB', 'TB'];
  private static readonly step = 1024;

  transform(bytes: number, fractionDigits = 1): unknown {
    if (!bytes) {
      return null;
    }
    let i: number;
    for (i = 0; i < FileSizePipe.dictionary.length; i++) {
      if (bytes < FileSizePipe.step) {
        break;
      }

      bytes = bytes / FileSizePipe.step;
    }

    return `${bytes.toFixed(fractionDigits)} ${FileSizePipe.dictionary[i]}`;
  }

}
