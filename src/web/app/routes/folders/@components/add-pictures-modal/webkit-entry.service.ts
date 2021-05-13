import {Injectable} from '@angular/core';
import {EMPTY, from, merge, Observable, of} from 'rxjs';
import {filter, map, mergeMap, toArray} from 'rxjs/operators';
import {filterEmpty} from '@utils/rx';

export interface DirectoryEntry {
  fullPath: string;
  name: string;
  files: File[];
}

const ROOT_DIR_TPL: DirectoryEntry = {fullPath: '/', name: 'This Folder', files: []};

@Injectable()
export class WebkitEntryService {

  dataTransferToDirectories(transfer: DataTransfer | null): Observable<DirectoryEntry> {
    if (!!transfer) {
      const items: DataTransferItem[] = Array.from(transfer.items);

      return merge(
        this.getRootFiles(items),
        this.getDirectories(items)
      );
    } else {
      return EMPTY;
    }
  }

  asRootDir(files: any[]): Observable<DirectoryEntry> {
    return of(ROOT_DIR_TPL.copy({files}));
  }

  mergeEntrySets(a: DirectoryEntry[], b: DirectoryEntry[]): DirectoryEntry[] {
    return a
      .map(ne => {
        const ee = b.find(e => ne.fullPath === e.fullPath);
        return !ee ? ne : ee.copy({files: ee.files.concat(ne.files).unique(f => f.name)});
      })
      .concat(b)
      .unique(e => e.fullPath);
  }

  private getRootFiles(dataTransferItems: DataTransferItem[]): Observable<DirectoryEntry> {
    return from(dataTransferItems)
      .pipe(
        map(it => it.getAsFile()),
        filterEmpty(),
        toArray(),
        map(files => ROOT_DIR_TPL.copy({files}))
      );
  }

  private getDirectories(dataTransferItems: DataTransferItem[]): Observable<DirectoryEntry> {
    return from(dataTransferItems)
      .pipe(
        map(dti => dti.webkitGetAsEntry()),
        filter(entry => entry !== null && entry.isDirectory),
        mergeMap(entry => from(this.flatMapDirectory(entry as FileSystemDirectoryEntry))),
        mergeMap(r => r)
      );
  }

  private async flatMapDirectory(entry: FileSystemDirectoryEntry): Promise<DirectoryEntry[]> {
    let currentEntry: DirectoryEntry = {
      fullPath: entry.fullPath,
      name: entry.name,
      files: [],
    };
    let children: DirectoryEntry[] = [];

    const directoryReader = entry.createReader();
    const dirEntries = await this.readAllEntries(directoryReader);

    for (const e of dirEntries) {
      if (!e) {
        continue;
      }
      if (e.isDirectory) {
        children = children.concat(await this.flatMapDirectory(e as FileSystemDirectoryEntry));
      } else {
        const file = await new Promise<File>((f, r) => (e as FileSystemFileEntry).file(f, r));
        currentEntry = currentEntry.copy({files: currentEntry.files.concat(file)});
      }
    }

    return [currentEntry, ...children];
  }

  private async readAllEntries(directoryReader: FileSystemDirectoryReader): Promise<FileSystemEntry[]> {
    let entries: FileSystemEntry[] = [];
    let current: FileSystemEntry[];

    do {
      current = await new Promise<FileSystemEntry[]>((r, f) => directoryReader.readEntries(r, f));
      entries = entries.concat(current);
    } while (current.length > 0);

    return entries;
  }
}
