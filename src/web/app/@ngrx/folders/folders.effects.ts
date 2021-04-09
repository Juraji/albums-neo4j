import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType, ROOT_EFFECTS_INIT} from '@ngrx/effects';
import {map, mergeMap, switchMap} from 'rxjs/operators';
import {EffectMarker} from '@utils/decorators';
import {
  createFolder,
  createFolderSuccess,
  deleteFolder,
  deleteFolderSuccess,
  loadFoldersTree,
  loadFoldersTreeSuccess,
  moveFolder,
  moveFolderSuccess,
  updateFolder,
  updateFolderSuccess
} from './folders.actions';
import {FoldersService} from '@services/folders.service';
import {of} from 'rxjs';


@Injectable()
export class FoldersEffects {

  @EffectMarker
  readonly loadRoots$ = createEffect(() => this.actions$.pipe(
    ofType(ROOT_EFFECTS_INIT, loadFoldersTree),
    switchMap(() => this.foldersService.getRoots()),
    map((tree) => loadFoldersTreeSuccess(tree))
  ));

  @EffectMarker
  readonly createFolder = createEffect(() => this.actions$.pipe(
    ofType(createFolder),
    switchMap(({parentId, folder}) => this.foldersService
      .createFolder(folder, parentId)
      .pipe(map(f => createFolderSuccess(f, parentId)))
    )
  ));

  @EffectMarker
  readonly updateFolder = createEffect(() => this.actions$.pipe(
    ofType(updateFolder),
    switchMap(({folder}) => this.foldersService.updateFolder(folder)),
    map(updateFolderSuccess)
  ));

  @EffectMarker
  readonly deleteFolder = createEffect(() => this.actions$.pipe(
    ofType(deleteFolder),
    switchMap(({folderId, recursive}) => this.foldersService.deleteFolder(folderId, recursive)
      .pipe(mergeMap(() => of(deleteFolderSuccess(folderId, recursive), loadFoldersTree())))),
  ));

  @EffectMarker
  readonly moveFolder = createEffect(() => this.actions$.pipe(
    ofType(moveFolder),
    switchMap(({folderId, targetId}) => this.foldersService.moveFolder(folderId, targetId)
      .pipe(map(() => moveFolderSuccess(folderId, targetId)))),
  ));

  constructor(
    private actions$: Actions,
    private foldersService: FoldersService,
  ) {
  }

}
