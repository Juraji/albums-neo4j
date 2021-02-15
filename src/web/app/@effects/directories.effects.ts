import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {loadRootDirectories, loadRootDirectoriesSuccess} from "@actions/directories.actions";
import {debounceTime, map, switchMap} from "rxjs/operators";
import {DirectoriesService} from "@services/directories.service";
import {AlbumEventsService} from "@services/album-events.service";


// noinspection JSUnusedGlobalSymbols
@Injectable()
export class DirectoriesEffects {

  constructor(
    private actions$: Actions,
    private directoriesService: DirectoriesService,
    private albumEventsService: AlbumEventsService
  ) {
  }

  readonly loadRoots$ = createEffect(() => this.actions$.pipe(
    ofType(loadRootDirectories),
    switchMap(() => this.directoriesService.getRoots()),
    map((tree) => loadRootDirectoriesSuccess({tree}))
  ))

  readonly rootUpdates$ = createEffect(() => this.albumEventsService
    .ofType<DirectoryTreeUpdatedEvent>("DirectoryTreeUpdatedEvent")
    .pipe(
      debounceTime(1000),
      map(() => loadRootDirectories())
    ))

}
