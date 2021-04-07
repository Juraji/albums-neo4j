import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType, ROOT_EFFECTS_INIT} from '@ngrx/effects';
import {DuplicatesService} from '@services/duplicates.service';
import {EffectMarker} from '@utils/decorators';
import {map, switchMap, tap} from 'rxjs/operators';
import {loadAllDuplicates, loadAllDuplicatesSuccess} from '@actions/duplicates.actions';

@Injectable()
export class DuplicatesEffects {

  @EffectMarker
  loadAllDuplicates$ = createEffect(() => this.actions$.pipe(
    ofType(ROOT_EFFECTS_INIT, loadAllDuplicates),
    switchMap(() => this.duplicatesService.getDuplicates()),
    map(loadAllDuplicatesSuccess)
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly duplicatesService: DuplicatesService,
  ) {
  }
}
