import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {ObserveProperty} from '@utils/decorators';
import {mergeMap, switchMap} from 'rxjs/operators';
import {Store} from '@ngrx/store';
import {
  deletePicture,
  loadPictureById,
  movePicture,
  selectFolderIdBiyPictureId,
  selectPictureById
} from '@ngrx/pictures';
import {unlinkDuplicate} from '@ngrx/duplicates';
import {FolderSelectorModal} from '@components/folder-selector';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {selectFolderById} from '@ngrx/folders';
import {filterEmpty} from '@utils/rx';
import {untilDestroyed} from '@utils/until-destroyed';

@Component({
  selector: 'app-duplicates-view',
  templateUrl: './duplicates-view.component.html',
  styleUrls: ['./duplicates-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DuplicatesViewComponent implements OnInit, OnDestroy {

  @Input()
  duplicate: DuplicatesView | null = null;

  @ObserveProperty('duplicate')
  readonly duplicate$ = new ReplaySubject<DuplicatesView>(1);

  readonly sourceFolder$ = this.duplicate$
    .pipe(
      switchMap(({sourceId}) => this.store.select(selectFolderIdBiyPictureId, {pictureId: sourceId})),
      filterEmpty(),
      switchMap((folderId) => this.store.select(selectFolderById, {folderId}))
    );

  readonly sourcePicture$ = this.duplicate$
    .pipe(switchMap(({sourceId}) => this.store.select(selectPictureById, {pictureId: sourceId})));

  readonly targetFolder$ = this.duplicate$
    .pipe(
      switchMap(({targetId}) => this.store.select(selectFolderIdBiyPictureId, {pictureId: targetId})),
      filterEmpty(),
      switchMap((folderId) => this.store.select(selectFolderById, {folderId}))
    );

  readonly targetPicture$ = this.duplicate$
    .pipe(switchMap(({targetId}) => this.store.select(selectPictureById, {pictureId: targetId})));

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
  ) {
  }

  ngOnInit() {
    this.duplicate$
      .pipe(
        untilDestroyed(this),
        mergeMap(d => [d.sourceId, d.targetId])
      )
      .subscribe(id => this.store.dispatch(loadPictureById(id)));
  }

  ngOnDestroy() {
  }

  onUnlinkDuplicate() {
    if (!!this.duplicate) {
      const msg = `Are you sure you want to unlink this duplicate?`;
      const {sourceId, targetId} = this.duplicate;

      this.modals.confirm(msg).onResolved
        .subscribe(() => this.store.dispatch(unlinkDuplicate(sourceId, targetId)));
    }
  }

  onDeletePicture(picture: Picture) {
    const msg = `Are you sure you want to delete ${picture.name}?`;

    this.modals.confirm(msg).onResolved
      .subscribe(() => this.store.dispatch(deletePicture(picture.id)));
  }

  onMovePicture(folder: Folder, picture: Picture) {
    this.modals.open<FolderSelectorResult>(FolderSelectorModal, {data: {source: folder}}).onResolved
      .subscribe(({target}) => this.store.dispatch(movePicture(picture.id, target.id)));
  }

  asPicture(tplInput: any): Picture {
    return tplInput;
  }

  asFolder(tplInput: any): Folder {
    return tplInput;
  }
}
