import {createAction} from '@ngrx/store';

export const loadFoldersTree = createAction(
  '[Folders] Load folders tree'
);

export const loadFoldersTreeSuccess = createAction(
  '[Folders] Load folders tree success',
  (tree: FolderTreeView[]) => ({tree})
);
