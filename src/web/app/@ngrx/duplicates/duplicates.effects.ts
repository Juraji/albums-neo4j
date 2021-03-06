import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType, ROOT_EFFECTS_INIT} from '@ngrx/effects';
import {DuplicatesService} from '@services/duplicates.service';
import {EffectMarker} from '@utils/decorators';
import {map, switchMap} from 'rxjs/operators';
import {
  duplicatesDetected,
  loadAllDuplicates,
  runDuplicateScan,
  unlinkDuplicate,
  unlinkDuplicateSuccess
} from './duplicates.actions';
import {PicturesService} from '@services/pictures.service';
import {switchMapContinue} from '@utils/rx';
import {Store} from '@ngrx/store';

@Injectable()
export class DuplicatesEffects {

  @EffectMarker
  loadAllDuplicates$ = createEffect(() => this.actions$.pipe(
    ofType(ROOT_EFFECTS_INIT, loadAllDuplicates),
    switchMap(() => this.duplicatesService.getDuplicates()),
    map(duplicatesDetected),
  ));

  @EffectMarker
  runDuplicateScan$ = createEffect(() => this.actions$.pipe(
    ofType(runDuplicateScan),
    switchMap(() => this.duplicatesService.runScan()),
    map(duplicatesDetected)
  ));

  @EffectMarker
  unlinkDuplicate$ = createEffect(() => this.actions$.pipe(
    ofType(unlinkDuplicate),
    switchMapContinue(({sourcePictureId, targetPictureId}) =>
      this.picturesService.deleteDuplicateFromPicture(sourcePictureId, targetPictureId)),
    map(([{sourcePictureId, targetPictureId}]) => unlinkDuplicateSuccess(sourcePictureId, targetPictureId))
  ));

  constructor(
    private readonly store: Store<AppState>,
    private readonly actions$: Actions,
    private readonly duplicatesService: DuplicatesService,
    private readonly picturesService: PicturesService,
  ) {
  }
}
