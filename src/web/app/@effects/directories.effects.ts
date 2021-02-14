import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {loadRootDirectories, loadRootDirectoriesSuccess} from "@actions/directories.actions";
import {map, switchMap} from "rxjs/operators";
import {DirectoriesService} from "@services/directories.service";


@Injectable()
export class DirectoriesEffects {

  constructor(
    private actions$: Actions,
    private directoriesService: DirectoriesService
  ) {
  }

  readonly loadRoots$ = createEffect(() => this.actions$.pipe(
    ofType(loadRootDirectories),
    switchMap(() => this.directoriesService.getRoots().pipe(
      map((tree) => loadRootDirectoriesSuccess({tree}))
    ))
  ))

}
