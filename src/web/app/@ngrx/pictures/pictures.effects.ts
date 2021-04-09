import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {Store} from '@ngrx/store';
import {map, switchMap} from 'rxjs/operators';
import {EffectMarker} from '@utils/decorators';
import {AlbumEventsService} from '@services/album-events.service';
import {FolderPicturesService} from '@services/folder-pictures.service';
import {loadPicturesByFolderId, loadPicturesByFolderIdSuccess} from './pictures.actions';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  fetchDirectoryPictures$ = createEffect(() => this.actions$.pipe(
    ofType(loadPicturesByFolderId),
    switchMap(({folderId}) => this.folderPicturesService.getFolderPictures(folderId)),
    map(loadPicturesByFolderIdSuccess)
  ));

  // @EffectMarker
  // fetchPicture$ = createEffect(() => this.actions$.pipe(
  //   ofType(fetchPicture),
  //   mergeMap(({pictureId}) => this.picturesService.getPicture(pictureId)),
  //   map((picture) => fetchPictureSuccess(picture))
  // ));
  //
  // @EffectMarker
  // onPictureUpdates$ = createEffect(() => {
  //     const onCreated$ = this.albumEventsService.ofType<PictureCreatedEvent>('PictureCreatedEvent');
  //     const onUpdated$ = this.albumEventsService.ofType<PictureUpdatedEvent>('PictureUpdatedEvent');
  //     const updates$ = merge(onCreated$, onUpdated$);
  //     const bufferEnd$ = updates$.pipe(debounceTime(1000));
  //
  //     return updates$.pipe(
  //       bufferWhen(() => bufferEnd$),
  //       switchMap(updates => iif(
  //         () => updates.length < 20,
  //         of(...updates.map(u => u.pictureId).unique().map(id => fetchPicture(id))),
  //         merge(
  //           of(...updates
  //             .map(u => u.directoryId)
  //             .filterEmpty()
  //             .unique()
  //             .map(id => fetchDirectoryPictures(id))),
  //           of(...updates
  //             .filter(u => !u.directoryId)
  //             .map(u => u.pictureId)
  //             .unique()
  //             .map(id => fetchPicture(id))),
  //         )
  //       )),
  //     );
  //   }
  // );
  //
  // @EffectMarker
  // addTagToPicture$ = createEffect(() => this.actions$.pipe(
  //   ofType(addTagToPicture),
  //   switchMap(({picture, tag}) => this.picturesService
  //     .addTag(picture.id, tag.id).pipe(mapTo({picture, tag}))),
  //   map(({picture, tag}) => addTagToPictureSuccess(
  //     picture.copy({tags: [...picture.tags, tag]}), tag))
  // ));
  //
  // @EffectMarker
  // removeTagFromPicture$ = createEffect(() => this.actions$.pipe(
  //   ofType(removeTagFromPicture),
  //   switchMap(({picture, tag}) => this.picturesService
  //     .removeTag(picture.id, tag.id).pipe(mapTo({picture, tag}))),
  //   map(({picture, tag}) => removeTagFromPictureSuccess(
  //     picture.copy({tags: picture.tags.filter(t => t.id !== tag.id)}), tag))
  // ));
  //
  // @EffectMarker
  // deletePicture$ = createEffect(() => this.actions$.pipe(
  //   ofType(deletePicture),
  //   switchMap(({pictureId, deleteFile}) => this.picturesService
  //     .deletePicture(pictureId, deleteFile)
  //     .pipe(mapTo(pictureId))),
  //   map(pictureId => deletePictureSuccess(pictureId))
  // ));

  constructor(
    private actions$: Actions,
    private store: Store<AppState>,
    private picturesService: PicturesService,
    private folderPicturesService: FolderPicturesService,
    private albumEventsService: AlbumEventsService
  ) {
  }

  // TODO: React to PictureCreatedEvent, should be debounced due to possible high rate of events
}
