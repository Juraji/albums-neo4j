import {createAction} from '@ngrx/store';

export const loadPicturesByFolderId = createAction(
  '[Pictures] Load pictures by folder id',
  (folderId: string) => ({folderId})
);

export const loadPicturesByFolderIdSuccess = createAction(
  '[Pictures] Load pictures by folder id success',
  (pictures: Picture[], folderId: string) => ({pictures, folderId})
);

export const loadPictureById = createAction(
  '[Pictures] Load picture by id',
  (pictureId: string) => ({pictureId})
);

export const loadPictureByIdSuccess = createAction(
  '[Pictures] Load picture by id success',
  (picture: Picture, folder: Folder) => ({picture, folder})
);

export const addPictureSuccess = createAction(
  '[Pictures] Add picture success',
  (picture: Picture, parentFolderId: string) => ({picture, parentFolderId})
);

export const updatePicture = createAction(
  '[Pictures] Update picture',
  (changes: Picture) => ({changes})
);

export const updatePictureSuccess = createAction(
  '[Pictures] Update picture success',
  (picture: Picture) => ({picture})
);

export const deletePicture = createAction(
  '[Pictures] Delete picture',
  (pictureId: string) => ({pictureId})
);

export const deletePictureSuccess = createAction(
  '[Pictures] Delete picture success',
  (pictureId: string) => ({pictureId})
);

export const movePicture = createAction(
  '[Pictures] Move picture',
  (pictureId: string, targetFolderId: string) => ({pictureId, targetFolderId})
);

export const movePictureSuccess = createAction(
  '[Pictures] Move picture success',
  (pictureId: string, targetFolderId: string) => ({pictureId, targetFolderId})
);
