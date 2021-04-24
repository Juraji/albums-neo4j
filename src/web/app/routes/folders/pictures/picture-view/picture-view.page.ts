import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute, Router} from '@angular/router';
import {map, switchMap, withLatestFrom} from 'rxjs/operators';
import {filterEmpty, not, once, switchMapContinue} from '@utils/rx';
import {selectFolderById} from '@ngrx/folders';
import {deletePicture, loadPictureById, movePicture, selectPictureById, updatePicture} from '@ngrx/pictures';
import {untilDestroyed} from '@utils/until-destroyed';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {FolderSelectorModal} from '@components/folder-selector';
import {EditPictureModal} from './@components/edit-picture/edit-picture.modal';
import {selectSetting, updateSetting} from '@ngrx/settings';

const MAXIMIZE_SETTING = 'picture-view--maximize-image';

@Component({
  templateUrl: './picture-view.page.html',
  styleUrls: ['./picture-view.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PictureViewPage implements OnInit, OnDestroy {

  public readonly folderId$ = this.activatedRoute.paramMap
    .pipe(map(m => m.get('folderId')), filterEmpty());

  public readonly pictureId$ = this.activatedRoute.paramMap
    .pipe(map(m => m.get('pictureId')), filterEmpty());

  public readonly folder$ = this.folderId$
    .pipe(
      switchMap(folderId => this.store.select(selectFolderById, {folderId})),
      filterEmpty()
    );

  public readonly picture$ = this.pictureId$
    .pipe(
      switchMap(pictureId => this.store.select(selectPictureById, {pictureId})),
      filterEmpty()
    );

  public readonly maximize$ = this.store
    .select(selectSetting(MAXIMIZE_SETTING, false));

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute,
    private readonly modals: Modals,
    private readonly router: Router,
  ) {
  }

  ngOnInit(): void {
    this.pictureId$
      .pipe(untilDestroyed(this))
      .subscribe(pictureId => this.store.dispatch(loadPictureById(pictureId)));
  }

  ngOnDestroy() {
  }

  onEditPicture() {
    this.picture$
      .pipe(once(),
        switchMap(data => this.modals.open<Picture>(EditPictureModal, {data}).onResolved),
        switchMapContinue(c => this.modals
          .confirm(`Are you sure you want to rename this picture to "${c.name}"?`).onResolved)
      )
      .subscribe(([changes]) => this.store.dispatch(updatePicture(changes)));
  }

  onMovePicture() {
    this.folder$
      .pipe(
        once(),
        switchMap(folder =>
          this.modals.open<FolderSelectorResult>(FolderSelectorModal, {data: {source: folder}}).onResolved),
        withLatestFrom(this.pictureId$)
      )
      .subscribe(([{target}, pictureId]) => {
        this.store.dispatch(movePicture(pictureId, target.id));
        this.router.navigate(['/folders', target.id, 'pictures', pictureId], {replaceUrl: true});
      });
  }

  onDeletePicture() {
    this.picture$
      .pipe(
        once(),
        switchMapContinue(p => this.modals.confirm(`Are you sure you want to delete "${p.name}"?`).onResolved),
      )
      .subscribe(([{id}]) => {
        this.store.dispatch(deletePicture(id));
        this.folderId$.pipe(once())
          .subscribe(fid => this.router.navigate(['/folders', fid]));
      });
  }

  onMaximize() {
    this.maximize$
      .pipe(once(), not())
      .subscribe(state => this.store.dispatch(updateSetting(MAXIMIZE_SETTING, state)));
  }
}
