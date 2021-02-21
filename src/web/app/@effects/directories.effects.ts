import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {loadRootDirectories, loadRootDirectoriesSuccess} from '@actions/directories.actions';
import {debounceTime, map, switchMap} from 'rxjs/operators';
import {DirectoriesService} from '@services/directories.service';
import {AlbumEventsService} from '@services/album-events.service';
import {EffectMarker} from '@utils/effect-marker.annotation';


@Injectable()
export class DirectoriesEffects {

  @EffectMarker
  readonly loadRoots$ = createEffect(() => this.actions$.pipe(
    ofType(loadRootDirectories),
    switchMap(() => this.directoriesService.getRoots()),
    map((tree) => loadRootDirectoriesSuccess(tree))
  ));

  @EffectMarker
  readonly rootUpdates$ = createEffect(() => this.albumEventsService
    .ofType<DirectoryTreeUpdatedEvent>('DirectoryTreeUpdatedEvent')
    .pipe(
      debounceTime(1000),
      map(() => loadRootDirectories())
    ));

  constructor(
    private actions$: Actions,
    private directoriesService: DirectoriesService,
    private albumEventsService: AlbumEventsService
  ) {
  }

}
