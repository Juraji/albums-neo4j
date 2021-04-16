import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {PicturesService} from '@services/pictures.service';
import {Store} from '@ngrx/store';
import {filter, map, share, switchMap, withLatestFrom} from 'rxjs/operators';
import {EffectMarker} from '@utils/decorators';
import {AlbumEventsService} from '@services/album-events.service';
import {FolderPicturesService} from '@services/folder-pictures.service';
import {loadPicturesByFolderId, loadPicturesByFolderIdSuccess} from './pictures.actions';
import {Subject} from 'rxjs';
import {HttpEventType, HttpResponse} from '@angular/common/http';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {filterEmpty} from '@utils/rx';


@Injectable()
export class PicturesEffects {

  @EffectMarker
  fetchDirectoryPictures$ = createEffect(() => this.actions$.pipe(
    ofType(loadPicturesByFolderId),
    switchMap(({folderId}) => {
      const progress = new Subject<number>();
      const shadeRef = this.modals.shade('Downloading pictures...', progress);
      const fetch = this.folderPicturesService.getFolderPictures(folderId).pipe(share());
      const cleanUp = () => {
        progress.complete();
        shadeRef.dismiss();
      };

      fetch.subscribe({
        next: e => {
          if (e.type === HttpEventType.DownloadProgress && !!e.total) {
            progress.next(e.total / e.loaded);
          }
        },
        error: cleanUp,
        complete: cleanUp
      });

      return fetch
        .pipe(
          filter(e => e.type === HttpEventType.Response),
          map(e => (e as HttpResponse<Picture[]>)?.body),
          filterEmpty(),
          withLatestFrom(pictures => ({pictures, folderId})),
        );
    }),
    map(({pictures, folderId}) => loadPicturesByFolderIdSuccess(pictures, folderId))
  ));

  constructor(
    private readonly actions$: Actions,
    private readonly store: Store<AppState>,
    private readonly picturesService: PicturesService,
    private readonly folderPicturesService: FolderPicturesService,
    private readonly albumEventsService: AlbumEventsService,
    private readonly modals: Modals,
  ) {
  }

  // TODO: React to PictureCreatedEvent, should be debounced due to possible high rate of events
}
