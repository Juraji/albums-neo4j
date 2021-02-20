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

