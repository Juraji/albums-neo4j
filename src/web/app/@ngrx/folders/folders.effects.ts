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
import {switchMapContinue} from '@utils/rx';


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
    switchMapContinue(({parentId, folder}) => this.foldersService.createFolder(folder, parentId)),
    map(([{parentId}, folder]) => createFolderSuccess(folder, parentId))
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
    switchMapContinue(({folderId, recursive}) => this.foldersService.deleteFolder(folderId, recursive)),
    mergeMap(([{folderId, recursive}]) => [
      deleteFolderSuccess(folderId, recursive),
      loadFoldersTree()
    ])
  ));

  @EffectMarker
  readonly moveFolder = createEffect(() => this.actions$.pipe(
    ofType(moveFolder),
    switchMapContinue(({folderId, targetId}) => this.foldersService.moveFolder(folderId, targetId)),
    map(([{folderId, targetId}]) => moveFolderSuccess(folderId, targetId))
  ));

  constructor(
    private actions$: Actions,
    private foldersService: FoldersService,
  ) {
  }

}
