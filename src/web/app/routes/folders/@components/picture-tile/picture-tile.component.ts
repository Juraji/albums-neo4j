import {Component, Input, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  foldersClearPictureSelection,
  foldersSelectAllSelectedPictures,
  foldersSelectIsSelectedByPictureId,
  foldersTogglePictureSelection
} from '../../@ngrx';
import {ObserveProperty} from '@utils/decorators';
import {ReplaySubject} from 'rxjs';
import {map, mergeMap, switchMap} from 'rxjs/operators';
import {FolderSelectorModal} from '@components/folder-selector';
import {deletePicture, movePicture} from '@ngrx/pictures';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {once, switchMapContinue} from '@utils/rx';
import {addTagToPicture} from '@ngrx/tags';

@Component({
  selector: 'app-picture-tile',
  templateUrl: './picture-tile.component.html',
  styleUrls: ['./picture-tile.component.scss']
})
export class PictureTileComponent implements OnInit {

  @Input()
  picture: BindingType<Picture>;

  @Input()
  folder: BindingType<Folder>;

  @ObserveProperty('picture')
  readonly picture$ = new ReplaySubject<Picture>(1);

  readonly isSelected$ = this.picture$.pipe(
    switchMap(({id}) => this.store.select(foldersSelectIsSelectedByPictureId, {pictureId: id}))
  );

  readonly selectedPictures$ = this.store.select(foldersSelectAllSelectedPictures);
  readonly selectedPicturesCount$ = this.selectedPictures$.pipe(map(it => it.length));
  readonly hasSelectedPictures$ = this.selectedPictures$.pipe(map(it => !it.isEmpty()));

  constructor(
    private readonly store: Store<FolderRouteAppState>,
    private readonly modals: Modals,
  ) {
  }

  ngOnInit(): void {
  }

  onSelectedToggle() {
    if (!!this.picture) {
      this.store.dispatch(foldersTogglePictureSelection(this.picture));
    }
  }

  onMoveSelf() {
    if (!!this.picture) {
      const pictureId = this.picture.id;
      this.modals.open<FolderSelectorResult>(FolderSelectorModal, {data: {source: this.folder}})
        .onResolved.subscribe(({target}) => this.store.dispatch(movePicture(pictureId, target.id)));
    }
  }

  onDeleteSelf() {
    if (!!this.picture) {
      const pictureId = this.picture.id;
      this.modals.confirm(`Are you sure you want to delete "${this.picture.name}"?`)
        .onResolved.subscribe(() => this.store.dispatch(deletePicture(pictureId)));
    }
  }

  onAddTagSelf(tag: Tag) {
    if (!!this.picture) {
      this.store.dispatch(addTagToPicture(this.picture.id, tag));
    }
  }

  onMoveSelection() {
    this.selectedPictures$
      .pipe(
        once(),
        switchMapContinue(() => this.modals.open<FolderSelectorResult>(
          FolderSelectorModal, {data: {source: this.folder}}).onResolved),
      )
      .subscribe(([pictures, {target}]) =>
        pictures.forEach(p => this.store.dispatch(movePicture(p.id, target.id))));
  }

  onDeleteSelection() {
    this.selectedPictures$
      .pipe(
        once(),
        switchMapContinue(pictures => this.modals.confirm(
          `Are you sure you want to delete the ${pictures.length} selected pictures?`).onResolved)
      )
      .subscribe(([pictures]) => pictures.forEach(p => this.store.dispatch(deletePicture(p.id))));
  }

  onAddTagSelection(tag: Tag) {
    this.selectedPictures$
      .pipe(once(), mergeMap(p => p))
      .subscribe(p => this.store.dispatch(addTagToPicture(p.id, tag)));
  }

  onClearSelection() {
    this.store.dispatch(foldersClearPictureSelection());
  }
}
