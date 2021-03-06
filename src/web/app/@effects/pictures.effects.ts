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
import {map, mapTo, switchMap} from 'rxjs/operators';
import {EffectMarker} from '@utils/effect-marker.annotation';


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
    switchMap(({pictureId}) => this.picturesService.getPicture(pictureId)),
    map((picture) => fetchPictureSuccess(picture))
  ));

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
  ) {
  }

  // TODO: React to PictureCreatedEvent, should be debounced due to possible high rate of events
}
