import {createAction, props} from '@ngrx/store';

export const loadPicturesSuccess = createAction(
  '[Pictures] Add pictures',
  props<{ directoryId: string; pictures: PictureProps[] }>()
);

export const setAllPicturesLoaded = createAction(
  '[Pictures] Set all pictures loaded',
  props<{ directoryId: string; pictures: PictureProps[] }>()
);

export const insurePictureRange = createAction(
  '[Pictures] Insure picture range',
  props<SelectPictureRangeProps>()
);

export const fetchPicture = createAction(
  '[Pictures] Fetch picture',
  props<FetchPictureProps>()
);

export const addTagToPicture = createAction(
  '[Pictures] Add tag to picture',
  props<AddTagToPictureProps>()
);

export const addTagToPictureSuccess = createAction(
  '[Pictures] Add tag to picture success',
  props<AddTagToPictureProps>()
);

export const removeTagFromPicture = createAction(
  '[Pictures] Remove tag from picture',
  props<AddTagToPictureProps>()
);

export const removeTagFromPictureSuccess = createAction(
  '[Pictures] Remove tag from picture success',
  props<AddTagToPictureProps>()
);
