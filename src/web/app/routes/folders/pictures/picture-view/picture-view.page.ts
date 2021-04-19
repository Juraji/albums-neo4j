import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute, Router} from '@angular/router';
import {map, switchMap, withLatestFrom} from 'rxjs/operators';
import {filterEmpty, once} from '@utils/rx';
import {selectFolderById} from '@ngrx/folders';
import {loadPictureById, movePicture, selectPictureById} from '@ngrx/pictures';
import {untilDestroyed} from '@utils/until-destroyed';
import {combineLatest} from 'rxjs';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {FolderSelectorModal} from '@components/folder-selector';

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

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute,
    private readonly modals: Modals,
    private readonly router: Router,
  ) {
  }

  ngOnInit(): void {
    combineLatest([this.pictureId$, this.folderId$])
      .pipe(untilDestroyed(this))
      .subscribe(([pictureId, folderId]) => this.store.dispatch(loadPictureById(pictureId, folderId)));
  }

  ngOnDestroy() {
  }

  onRenamePicture() {
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
        this.router.navigate(['/folders', target.id, 'pictures', pictureId]);
      });
  }

  onDeletePicture() {
  }
}
