import {createAction, props} from '@ngrx/store';

export const fetchPicture = createAction(
  '[Pictures] Fetch picture',
  props<FetchPictureProps>()
);

export const fetchPictureSuccess = createAction(
  '[Pictures] Fetch picture success',
  props<PictureProps>()
);

export const fetchDirectoryPictures = createAction(
  '[Pictures] Fetch directory pictures',
  props<FetchDirectoryPicturesProps>()
);

export const fetchDirectoryPicturesSuccess = createAction(
  '[Pictures] Fetch directory pictures success',
  props<FetchDirectoryPicturesSuccessProps>()
);

export const addTagToPicture = createAction(
  '[Pictures] Add tag to picture',
  props<AddTagToPictureProps>()
);

export const addTagToPictureSuccess = createAction(
  '[Pictures] Add tag to picture success',
  props<PictureProps>()
);

export const removeTagFromPicture = createAction(
  '[Pictures] Remove tag from picture',
  props<AddTagToPictureProps>()
);

export const removeTagFromPictureSuccess = createAction(
  '[Pictures] Remove tag from picture success',
  props<PictureProps>()
);

export const setDirectoryLoadState = createAction(
  '[Pictures] Set directory load state',
  props<SetDirectoryLoadStateProps>()
);
