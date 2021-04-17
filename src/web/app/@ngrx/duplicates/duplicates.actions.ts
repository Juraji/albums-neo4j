import {createAction} from '@ngrx/store';

export const loadAllDuplicates = createAction(
  '[Duplicates] Load all duplicates'
);

export const loadAllDuplicatesSuccess = createAction(
  '[Duplicates] Load all duplicates success',
  (duplicates: DuplicatesView[]) => ({duplicates})
);

export const unlinkDuplicate = createAction(
  '[Duplicates] Unlink duplicate',
  (sourcePictureId: string, targetPictureId: string) => ({sourcePictureId, targetPictureId})
);

export const unlinkDuplicateSuccess = createAction(
  '[Duplicates] Unlink duplicate success',
  (sourcePictureId: string, targetPictureId: string) => ({sourcePictureId, targetPictureId})
);
