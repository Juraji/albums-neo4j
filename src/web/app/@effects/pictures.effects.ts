import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {insurePictureRange, loadPicturesSuccess} from '@actions/pictures.actions';
import {Store} from '@ngrx/store';
import {filter, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {selectLoadedPictureCount} from '@reducers/pictures';
import {of} from 'rxjs';
import {EffectMarker} from '@utils/effect-marker.annotation';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  insurePictureSetRange$ = createEffect(() => this.actions$.pipe(
    ofType(insurePictureRange),
    switchMap((props) => of(props).pipe(withLatestFrom(this.store.select(selectLoadedPictureCount, props)))),
    filter(([{page, size}, currentCount]) => currentCount < (page * size + size)),
    switchMap(([{directoryId, page, size}]) =>
      this.picturesService.getPicturesByDirectory(directoryId, page, size)
        .pipe(map((pictures) => ({directoryId, pictures})))),
    map((props) => loadPicturesSuccess(props))
  ));

  constructor(
    private actions$: Actions,
    private store: Store<AppState>,
    private picturesService: PicturesService,
  ) {
  }

  // TODO: React to PictureCreatedEvent, should be debounced due to possible high rate of events
}
