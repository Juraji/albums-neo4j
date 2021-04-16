import {createAction} from '@ngrx/store';

export const loadPicturesByFolderId = createAction(
  '[Pictures] Load pictures by folder id',
  (folderId: string) => ({folderId})
);

export const loadPicturesByFolderIdSuccess = createAction(
  '[Pictures] Load pictures by folder id success',
  (pictures: Picture[], folderId: string) => ({pictures, folderId})
);

export const addPicture = createAction(
  '[Pictures] Add picture',
  (picture: Omit<Picture, 'id'>, parentFolderId: string) => ({picture, parentFolderId})
);

export const addPictureSuccess = createAction(
  '[Pictures] Add picture success',
  (picture: Picture, parentFolderId: string) => ({picture, parentFolderId})
);
