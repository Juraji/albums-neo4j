import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {Store} from '@ngrx/store';
import {distinctUntilKeyChanged, map, mergeMap} from 'rxjs/operators';
import {EffectMarker} from '@utils/decorators';
import {FolderPicturesService} from '@services/folder-pictures.service';
import {
  deletePicture,
  deletePictureSuccess,
  loadPictureById,
  loadPictureByIdSuccess,
  loadPicturesByFolderId,
  loadPicturesByFolderIdSuccess,
  movePicture,
  movePictureSuccess
} from './pictures.actions';
import {filterAsync, isNullOrUndefined, switchMapContinue} from '@utils/rx';
import {selectPictureById} from '@ngrx/pictures/pictures.reducer';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  fetchDirectoryPictures$ = createEffect(() => this.actions$.pipe(
    ofType(loadPicturesByFolderId),
    distinctUntilKeyChanged('folderId'),
    switchMapContinue(({folderId}) => this.folderPicturesService.getFolderPictures(folderId)),
    map(([{folderId}, pictures]) => loadPicturesByFolderIdSuccess(pictures, folderId))
  ));

  @EffectMarker
  loadPictureById$ = createEffect(() => this.actions$.pipe(
    ofType(loadPictureById),
    filterAsync(({pictureId}) =>
      this.store.select(selectPictureById, {pictureId}).pipe(isNullOrUndefined())),
    mergeMap(({pictureId}) => this.picturesService.getPicture(pictureId)),
    map(({picture, folder}) => loadPictureByIdSuccess(picture, folder))
  ));

  @EffectMarker
  movePicture$ = createEffect(() => this.actions$.pipe(
    ofType(movePicture),
    switchMapContinue(({pictureId, targetFolderId}) => this.picturesService.movePicture(pictureId, targetFolderId)),
    map(([{pictureId, targetFolderId}]) => movePictureSuccess(pictureId, targetFolderId))
  ));

  @EffectMarker
  deletePicture = createEffect(() => this.actions$.pipe(
    ofType(deletePicture),
    switchMapContinue(({pictureId}) => this.picturesService.deletePicture(pictureId)),
    map(([{pictureId}]) => deletePictureSuccess(pictureId))
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly store: Store<AppState>,
    private readonly picturesService: PicturesService,
    private readonly folderPicturesService: FolderPicturesService,
  ) {
  }
}
