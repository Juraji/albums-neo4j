import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {filter, map, switchMap} from 'rxjs/operators';
import {selectFolderById, selectFolderChildrenById, selectRootFolders} from '@ngrx/folders';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {filterEmpty, filterEmptyArray} from '@utils/rx';
import {ROOT_FOLDER, ROOT_FOLDER_ID} from '../root-folder';
import {loadPicturesByFolderId, selectPicturesByFolderId} from '@ngrx/pictures';
import {untilDestroyed} from '@utils/until-destroyed';

@Component({
  templateUrl: './folder.page.html',
  styleUrls: ['./folder.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FolderPage implements OnInit, OnDestroy {
  private pictureRange$ = new BehaviorSubject<PageRange>({start: 0, end: 0});

  public readonly folderId$ = this.activatedRoute.paramMap
    .pipe(map(m => m.get('folderId')), filterEmpty());

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
  ) {
  }

  ngOnInit(): void {
    this.folderId$
      .pipe(filter(id => id !== ROOT_FOLDER_ID), untilDestroyed(this))
      .subscribe(folderId => this.store.dispatch(loadPicturesByFolderId(folderId)));
  }

  ngOnDestroy() {
  }

  onPageUpdate(range: PageRange) {
    this.pictureRange$.next(range);
  }
}
