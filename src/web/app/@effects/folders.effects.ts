import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType, ROOT_EFFECTS_INIT} from '@ngrx/effects';
import {map, switchMap} from 'rxjs/operators';
import {EffectMarker} from '@utils/decorators';
import {loadFoldersTree, loadFoldersTreeSuccess} from '@actions/folders.actions';
import {FoldersService} from '@services/folders.service';


@Injectable()
export class FoldersEffects {

  @EffectMarker
  readonly loadRoots$ = createEffect(() => this.actions$.pipe(
    ofType(ROOT_EFFECTS_INIT, loadFoldersTree),
    switchMap(() => this.foldersService.getRoots()),
    map((tree) => loadFoldersTreeSuccess(tree))
  ));

  constructor(
    private actions$: Actions,
    private foldersService: FoldersService,
  ) {
  }

}
