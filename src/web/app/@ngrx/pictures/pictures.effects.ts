import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {Store} from '@ngrx/store';
import {filter, map, share, switchMap} from 'rxjs/operators';
import {EffectMarker} from '@utils/decorators';
import {AlbumEventsService} from '@services/album-events.service';
import {FolderPicturesService} from '@services/folder-pictures.service';
import {
  deletePicture,
  deletePictureSuccess,
  loadPictureById,
  loadPicturesByFolderId,
  loadPicturesByFolderIdSuccess,
  movePicture,
  movePictureSuccess
} from './pictures.actions';
import {Subject} from 'rxjs';
import {HttpEventType, HttpResponse} from '@angular/common/http';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {filterAsync, filterEmpty, isNullOrUndefined, switchMapContinue} from '@utils/rx';
import {selectPictureById} from '@ngrx/pictures/pictures.reducer';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  fetchDirectoryPictures$ = createEffect(() => this.actions$.pipe(
    ofType(loadPicturesByFolderId),
    switchMap(({folderId}) => {
      const progress = new Subject<number>();
      const shadeRef = this.modals.shade('Downloading pictures...', progress);
      const fetch = this.folderPicturesService.getFolderPictures(folderId).pipe(share());
      const cleanUp = () => {
        progress.complete();
        shadeRef.dismiss();
      };

      fetch.subscribe({
        next: e => {
          if (e.type === HttpEventType.DownloadProgress && !!e.total) {
            progress.next((e.loaded / e.total));
          }
        },
        error: cleanUp,
        complete: cleanUp
      });

      return fetch
        .pipe(
          filter(e => e.type === HttpEventType.Response),
          map(e => (e as HttpResponse<Picture[]>)?.body),
          filterEmpty(),
          map(pictures => ({pictures, folderId})),
        );
    }),
    map(({pictures, folderId}) => loadPicturesByFolderIdSuccess(pictures, folderId))
  ));

  @EffectMarker
  loadPictureById$ = createEffect(() => this.actions$.pipe(
    ofType(loadPictureById),
    filterAsync(({pictureId}) => this.store.select(selectPictureById, {pictureId})
      .pipe(isNullOrUndefined())),
    map(({folderId}) => loadPicturesByFolderId(folderId))
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
    private readonly albumEventsService: AlbumEventsService,
    private readonly modals: Modals,
  ) {
  }
}
