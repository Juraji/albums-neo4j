import {createAction} from '@ngrx/store';

export const loadRootDirectories = createAction(
  '[Directories] Load Root Directories'
);

export const loadRootDirectoriesSuccess = createAction(
  '[Directories] Load Root Directories Success',
  (tree: Directory[]) => ({tree})
);
