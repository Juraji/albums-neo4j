import {createAction} from '@ngrx/store';

export const fetchPicture = createAction(
  '[Pictures] Fetch picture',
  (pictureId: string) => ({pictureId})
);

export const fetchPictureSuccess = createAction(
  '[Pictures] Fetch picture success',
  (picture: PictureProps) => ({picture})
);

export const fetchDirectoryPictures = createAction(
  '[Pictures] Fetch directory pictures',
  (directoryId: string) => ({directoryId})
);

export const fetchDirectoryPicturesSuccess = createAction(
  '[Pictures] Fetch directory pictures success',
  (pictures: PictureProps[]) => ({pictures})
);

export const addTagToPicture = createAction(
  '[Pictures] Add tag to picture',
  (picture: PictureProps, tag: Tag) => ({picture, tag})
);

export const addTagToPictureSuccess = createAction(
  '[Pictures] Add tag to picture success',
  (picture: PictureProps, tag: Tag) => ({picture, tag})
);

export const removeTagFromPicture = createAction(
  '[Pictures] Remove tag from picture',
  (picture: PictureProps, tag: Tag) => ({picture, tag})
);

export const removeTagFromPictureSuccess = createAction(
  '[Pictures] Remove tag from picture success',
  (picture: PictureProps, tag: Tag) => ({picture, tag})
);
