import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {
  addTagToPicture,
  addTagToPictureSuccess,
  fetchPicture,
  insurePictureRange,
  loadPicturesSuccess, removeTagFromPicture, removeTagFromPictureSuccess,
  setAllPicturesLoaded
} from '@actions/pictures.actions';
import {Store} from '@ngrx/store';
import {map, mapTo, switchMap} from 'rxjs/operators';
import {selectLoadedPictureCount} from '@reducers/pictures';
import {EffectMarker} from '@utils/effect-marker.annotation';
import {filterAsync} from '@utils/rx/filter-async';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  insurePictureSetRange$ = createEffect(() => this.actions$.pipe(
    ofType(insurePictureRange),
    filterAsync((p) => this.store.select(selectLoadedPictureCount, p)
      .pipe(map((c) => c < (p.page * p.size) + p.size))),
    switchMap(({directoryId, page, size}) =>
      this.picturesService.getPicturesByDirectory(directoryId, page, size)
        .pipe(map((pictures) => ({directoryId, pictures})))),
    map((props) => {
      if (props.pictures.length === 0) {
        return setAllPicturesLoaded(props);
      } else {
        return loadPicturesSuccess(props);
      }
    })
  ));

  @EffectMarker
  fetchPicture$ = createEffect(() => this.actions$.pipe(
    ofType(fetchPicture),
    switchMap(({pictureId}) =>
      this.picturesService.getPicture(pictureId)
        .pipe(map(picture => ({directoryId: picture.directory.id, pictures: [picture]})))),
    map((props) => loadPicturesSuccess(props))
  ));

  @EffectMarker
  addTagToPicture$ = createEffect(() => this.actions$.pipe(
    ofType(addTagToPicture),
    switchMap(({picture, tag}) => this.picturesService.addTag(picture.id, tag.id).pipe(mapTo({picture, tag}))),
    map((props) => addTagToPictureSuccess(props))
  ));

  @EffectMarker
  removeTagFromPicture$ = createEffect(() => this.actions$.pipe(
    ofType(removeTagFromPicture),
    switchMap(({picture, tag}) => this.picturesService.removeTag(picture.id, tag.id).pipe(mapTo({picture, tag}))),
    map((props) => removeTagFromPictureSuccess(props))
  ));

  constructor(
    private actions$: Actions,
    private store: Store<AppState>,
    private picturesService: PicturesService,
  ) {
  }

  // TODO: React to PictureCreatedEvent, should be debounced due to possible high rate of events
}
