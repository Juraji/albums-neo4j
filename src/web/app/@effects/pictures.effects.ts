import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {insurePictureRange, loadPicturesSuccess, setAllPicturesLoaded} from '@actions/pictures.actions';
import {Store} from '@ngrx/store';
import {map, switchMap} from 'rxjs/operators';
import {selectLoadedPictureCount} from '@reducers/pictures';
import {EffectMarker} from '@utils/effect-marker.annotation';
import {filterAsync} from '@utils/filter-async.rx-pipe';


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

  constructor(
    private actions$: Actions,
    private store: Store<AppState>,
    private picturesService: PicturesService,
  ) {
  }

  // TODO: React to PictureCreatedEvent, should be debounced due to possible high rate of events
}
