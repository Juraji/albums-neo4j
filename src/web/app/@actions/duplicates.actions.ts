import {createAction} from '@ngrx/store';

export const fetchAllDuplicates = createAction(
  '[Duplicates] Fetch All Duplicates'
);

export const fetchAllDuplicatesSuccess = createAction(
  '[Duplicates] Fetch All Duplicates Success',
  (duplicates: DuplicateProps[]) => ({duplicates})
);

export const scanDuplicatesForPicture = createAction(
  '[Duplicates] Scan Duplicates',
  (pictureId: string) => ({pictureId})
);

export const scanDuplicatesForPictureSuccess = createAction(
  '[Duplicates] Scan Duplicates Success',
  (duplicates: DuplicateProps[]) => ({duplicates})
);

export const unlinkDuplicate = createAction(
  '[Duplicates] Unlink Duplicate',
  (duplicateId: string) => ({duplicateId})
);

export const unlinkDuplicateSuccess = createAction(
  '[Duplicates] Unlink Duplicate Success',
  (duplicateId: string) => ({duplicateId})
);
