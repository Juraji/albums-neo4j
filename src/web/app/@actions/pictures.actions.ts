import {createAction} from '@ngrx/store';

export const loadPicturesByFolderId = createAction(
  '[Pictures] Load pictures by folder id',
  (folderId: string) => ({folderId})
);

export const loadPicturesByFolderIdSuccess = createAction(
  '[Pictures] Load pictures by folder id success',
  (pictures: Picture[]) => ({pictures})
);
