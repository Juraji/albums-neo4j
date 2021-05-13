import {ChangeDetectionStrategy, Component, HostBinding, Inject} from '@angular/core';
import {MODAL_DATA, ModalRef, Modals} from '@juraji/ng-bootstrap-modals';
import {FolderPicturesService} from '@services/folder-pictures.service';
import {concatMap, finalize, map, mergeMap, toArray, withLatestFrom} from 'rxjs/operators';
import {DirectoryEntry, WebkitEntryService} from './webkit-entry.service';
import {BehaviorSubject} from 'rxjs';
import {once} from '@utils/rx';
import {Store} from '@ngrx/store';
import {FoldersService} from '@services/folders.service';
import {loadFoldersTree} from '@ngrx/folders';
import {addPictureSuccess} from '@ngrx/pictures';

const ALLOWED_FILE_TYPES = [
  'image/jpeg',
  'image/bmp',
  'image/gif',
  'image/png',
  'image/tiff',
];

@Component({
  templateUrl: './add-pictures.modal.html',
  styleUrls: ['./add-pictures.modal.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [WebkitEntryService]
})
export class AddPicturesModal {

  @HostBinding('class.dragging')
  public isDragging = false;

  public readonly selectedDirectories$ = new BehaviorSubject<DirectoryEntry[]>([]);

  constructor(
    private readonly modals: Modals,
    private readonly modalRef: ModalRef,
    private readonly store: Store<AppState>,
    private readonly foldersService: FoldersService,
    private readonly folderPicturesService: FolderPicturesService,
    private readonly webkitEntryService: WebkitEntryService,
    @Inject(MODAL_DATA) public readonly parentFolder: Folder
  ) {
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  async onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;

    this.webkitEntryService
      .dataTransferToDirectories(event.dataTransfer)
      .pipe(
        map(entry => entry.copy({
          files: entry.files.filter(it => ALLOWED_FILE_TYPES.includes(it.type))
        })),
        toArray(),
        withLatestFrom(this.selectedDirectories$),
        map(([newEntries, existingEntries]) =>
          this.webkitEntryService.mergeEntrySets(newEntries, existingEntries)),
      )
      .subscribe(entries => this.selectedDirectories$.next(entries));
  }

  onFileBrowse(event: Event) {
    event.preventDefault();
    event.stopPropagation();

    const files = Array.from((event.target as HTMLInputElement).files || []);
    this.webkitEntryService.asRootDir(files)
      .pipe(
        withLatestFrom(this.selectedDirectories$),
        map(([newEntry, existingEntries]) =>
          this.webkitEntryService.mergeEntrySets([newEntry], existingEntries)),
      )
      .subscribe(entries => this.selectedDirectories$.next(entries));
  }

  onRemoveDirectory(dirIdx: number) {
    this.selectedDirectories$
      .pipe(once(), map(arr => arr.removeAt(dirIdx)))
      .subscribe(entries => this.selectedDirectories$.next(entries));
  }

  onRemoveFile(dirIdx: number, fileIdx: number) {
    this.selectedDirectories$
      .pipe(once(), map(arr => {
        const entry = arr[dirIdx].copy(e => ({files: e.files.removeAt(fileIdx)}));
        return arr.replace(dirIdx, entry);
      }))
      .subscribe(entries => this.selectedDirectories$.next(entries));
  }

  onSubmit(event: Event) {
    event.preventDefault();
    event.stopPropagation();

    const parentFolderId = this.parentFolder.id;

    this.selectedDirectories$
      .pipe(
        once(),
        mergeMap(x => x),
        concatMap(entry => this.foldersService.createFolderP(entry.fullPath, parentFolderId)
          .pipe(map(folder => ({folder, entry})))),
        concatMap(({folder, entry}) => this.folderPicturesService.uploadPictures(folder, entry.files)
          .pipe(map(pictures => ({folder, pictures})))),
        finalize(() => {
          this.store.dispatch(loadFoldersTree());
          this.modalRef.resolve();
        }),
      )
      .subscribe(({folder, pictures}) => pictures.forEach(p => this.store.dispatch(addPictureSuccess(p, folder.id))));
  }
}
