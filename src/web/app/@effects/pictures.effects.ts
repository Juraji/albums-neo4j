import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {
  addTagToPicture,
  addTagToPictureSuccess,
  fetchDirectoryPictures,
  fetchDirectoryPicturesSuccess,
  fetchPicture,
  fetchPictureSuccess,
  removeTagFromPicture,
  removeTagFromPictureSuccess,
} from '@actions/pictures.actions';
import {Store} from '@ngrx/store';
import {bufferWhen, debounceTime, map, mapTo, mergeMap, switchMap} from 'rxjs/operators';
import {EffectMarker} from '@utils/effect-marker.annotation';
import {AlbumEventsService} from '@services/album-events.service';
import {iif, merge, of} from 'rxjs';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  fetchDirectoryPictures$ = createEffect(() => this.actions$.pipe(
    ofType(fetchDirectoryPictures),
    switchMap(({directoryId}) =>
      this.picturesService.getPicturesByDirectory(directoryId)),
    map((pictures) => fetchDirectoryPicturesSuccess(pictures))
  ));

  @EffectMarker
  fetchPicture$ = createEffect(() => this.actions$.pipe(
    ofType(fetchPicture),
    mergeMap(({pictureId}) => this.picturesService.getPicture(pictureId)),
    map((picture) => fetchPictureSuccess(picture))
  ));

  @EffectMarker
  onPictureUpdates$ = createEffect(() => {
      const onCreated$ = this.albumEventsService.ofType<PictureCreatedEvent>('PictureCreatedEvent');
      const onUpdated$ = this.albumEventsService.ofType<PictureUpdatedEvent>('PictureUpdatedEvent');
      const updates$ = merge(onCreated$, onUpdated$);
      const bufferEnd$ = updates$.pipe(debounceTime(1000));

      return updates$.pipe(
        bufferWhen(() => bufferEnd$),
        switchMap(updates => iif(
          () => updates.length < 20,
          of(...updates.map(u => u.pictureId).unique().map(id => fetchPicture(id))),
          merge(
            of(...updates
              .map(u => u.directoryId)
              .filterEmpty()
              .unique()
              .map(id => fetchDirectoryPictures(id))),
            of(...updates
              .filter(u => !u.directoryId)
              .map(u => u.pictureId)
              .unique()
              .map(id => fetchPicture(id))),
          )
        )),
      );
    }
  );

  @EffectMarker
  addTagToPicture$ = createEffect(() => this.actions$.pipe(
    ofType(addTagToPicture),
    switchMap(({picture, tag}) => this.picturesService
      .addTag(picture.id, tag.id).pipe(mapTo({picture, tag}))),
    map(({picture, tag}) => addTagToPictureSuccess(
      picture.copy({tags: [...picture.tags, tag]}), tag))
  ));

  @EffectMarker
  removeTagFromPicture$ = createEffect(() => this.actions$.pipe(
    ofType(removeTagFromPicture),
    switchMap(({picture, tag}) => this.picturesService
      .removeTag(picture.id, tag.id).pipe(mapTo({picture, tag}))),
    map(({picture, tag}) => removeTagFromPictureSuccess(
      picture.copy({tags: picture.tags.filter(t => t.id !== tag.id)}), tag))
  ));

  constructor(
    private actions$: Actions,
    private store: Store<AppState>,
    private picturesService: PicturesService,
    private albumEventsService: AlbumEventsService
  ) {
  }

  // TODO: React to PictureCreatedEvent, should be debounced due to possible high rate of events
}
