import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {AlbumEventsService} from '@services/album-events.service';
import {DuplicatesService} from '@services/duplicates.service';
import {EffectMarker} from '@utils/decorators';
import {
  fetchAllDuplicates,
  fetchAllDuplicatesSuccess,
  scanDuplicatesForPicture,
  scanDuplicatesForPictureSuccess,
  unlinkDuplicate,
  unlinkDuplicateSuccess
} from '@actions/duplicates.actions';
import {map, mapTo, mergeMap, switchMap} from 'rxjs/operators';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {EMPTY} from 'rxjs';

@Injectable()
export class DuplicatesEffects {

  @EffectMarker
  loadAllDuplicates$ = createEffect(() => this.actions$.pipe(
    ofType(fetchAllDuplicates),
    switchMap(() => this.duplicatesService.getAllDuplicates()),
    map(fetchAllDuplicatesSuccess)
  ));

  @EffectMarker
  scanDuplicatesForPicture$ = createEffect(() => this.actions$.pipe(
    ofType(scanDuplicatesForPicture),
    switchMap(({pictureId}) => {
      const shadeRef = this.modals.shade('Scanning duplicates...', EMPTY);
      const result = this.duplicatesService.scanDuplicates(pictureId);
      result.subscribe({complete: () => shadeRef.dismiss()});
      return result;
    }),
    map(scanDuplicatesForPictureSuccess)
  ));

  @EffectMarker
  unlinkDuplicate$ = createEffect(() => this.actions$.pipe(
    ofType(unlinkDuplicate),
    mergeMap(({duplicateId}) => this.duplicatesService.unlinkDuplicate(duplicateId).pipe(mapTo(duplicateId))),
    map(id => unlinkDuplicateSuccess(id))
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly duplicatesService: DuplicatesService,
    private readonly albumEventsService: AlbumEventsService,
    private readonly modals: Modals,
  ) {
  }
}
