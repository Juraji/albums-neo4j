import {ChangeDetectionStrategy, Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute, Router} from '@angular/router';
import {filter, finalize, last, map, mergeMap, share, switchMap} from 'rxjs/operators';
import {
  createFolder,
  deleteFolder,
  moveFolder,
  selectFolderById,
  selectFolderChildrenById,
  selectRootFolders
} from '@ngrx/folders';
import {BehaviorSubject, combineLatest, from, Observable, of} from 'rxjs';
import {filterEmpty, filterEmptyArray, once, switchMapContinue} from '@utils/rx';
import {addPictureSuccess, loadPicturesByFolderId, selectPicturesByFolderId} from '@ngrx/pictures';
import {untilDestroyed} from '@utils/until-destroyed';
import {ROOT_FOLDER, ROOT_FOLDER_ID} from '@services/folders.service';
import {AddFolderModal} from '../@components/add-folder-modal/add-folder.modal';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {AddPicturesModal} from '../@components/add-pictures-modal/add-pictures.modal';
import {runDuplicateScan} from '@ngrx/duplicates';
import {FolderSelectorModal} from '@components/folder-selector';

@Component({
  templateUrl: './folder.page.html',
  styleUrls: ['./folder.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FolderPage implements OnInit, OnDestroy {
  private addPicturesDisplayed = false;

  private readonly pictureRange$ = new BehaviorSubject<PageRange>({start: 0, end: 0});

  public readonly folderId$ = this.activatedRoute.paramMap
    .pipe(map(m => m.get('folderId')), filterEmpty());

  readonly isNotRootFolder$ = this.folderId$.pipe(map(id => id !== ROOT_FOLDER_ID));

  public readonly folder$ = this.folderId$.pipe(
    switchMap(folderId => folderId === ROOT_FOLDER_ID
      ? of(ROOT_FOLDER)
      : this.store.select(selectFolderById, {folderId}))
  );

  public readonly childFolders$ = this.folderId$
    .pipe(
      switchMap((folderId) => folderId === ROOT_FOLDER_ID
        ? this.store.select(selectRootFolders)
        : this.store.select(selectFolderChildrenById, {folderId})),
      filterEmptyArray(),
      map(folders => folders.sort((a, b) => a.name.localeCompare(b.name)))
    );

  public readonly pictures$: Observable<Picture[]> = this.folderId$
    .pipe(
      switchMap(folderId => this.store.select(selectPicturesByFolderId, {folderId})),
      map(pictures => pictures.sort((a, b) => a.name.localeCompare(b.name)))
    );

  public readonly pagedPictures$ = combineLatest([this.pictures$, this.pictureRange$])
    .pipe(map(([pictures, range]) => pictures.slice(range.start, range.end)));

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute,
    private readonly modals: Modals,
    private readonly router: Router,
  ) {
  }

  ngOnInit(): void {
    this.folderId$
      .pipe(filter(id => id !== ROOT_FOLDER_ID), untilDestroyed(this))
      .subscribe(folderId => this.store.dispatch(loadPicturesByFolderId(folderId)));
  }

  ngOnDestroy() {
  }

  @HostListener('window:dragover', ['$event'])
  onDragEnter(e: DragEvent) {
    if (e.dataTransfer?.types[0] === 'Files' && !this.addPicturesDisplayed) {
      this.onAddPictures();
    }
  }

  onPageUpdate(range: PageRange) {
    this.pictureRange$.next(range);
  }

  onAddFolder() {
    this.folderId$
      .pipe(
        once(),
        map(fid => fid === ROOT_FOLDER_ID ? undefined : fid),
        switchMapContinue(() => this.modals.open<Folder>(AddFolderModal).onResolved)
      )
      .subscribe(([parentId, folder]) => this.store.dispatch(createFolder(folder, parentId)));
  }

  onAddPictures() {
    this.addPicturesDisplayed = true;

    const pictures$ = this.folder$
      .pipe(
        once(),
        filterEmpty(),
        switchMapContinue(data => this.modals.open<Picture[]>(AddPicturesModal, {data}).onResolved),
        mergeMap(([folder, pictures]) =>
          from(pictures).pipe(map(picture => ({folder, picture})))),
        finalize(() => this.addPicturesDisplayed = false),
        share(),
      );

    pictures$
      .pipe(
        last(),
        mergeMap(() => this.modals
          .confirm('Upload complete, do you want to scan for duplicates').onResolved)
      )
      .subscribe({
        next: () => {
          this.store.dispatch(runDuplicateScan());
          this.router.navigate(['/duplicates']);
        },
        error: () => null
      });

    pictures$.subscribe(({folder, picture}) =>
      this.store.dispatch(addPictureSuccess(picture, folder.id)));
  }

  onMoveFolder() {
    this.folder$
      .pipe(
        once(),
        switchMap(source => this.modals.open<FolderSelectorResult>(
          FolderSelectorModal,
          {data: {source, enableRoot: true}}
        ).onResolved)
      )
      .subscribe(({source, target}) =>
        this.store.dispatch(moveFolder(source.id, target.id)));
  }

  onDeleteFolder() {
    this.folder$
      .pipe(
        once(),
        filterEmpty(),
        switchMapContinue(f => this.modals
          .confirm(`Are you sure you want to delete the folder "${f.name}"?`).onResolved),
      )
      .subscribe(([{id}]) => {
        this.store.dispatch(deleteFolder(id, true));
        this.router.navigate(['/folders']);
      });
  }
}
