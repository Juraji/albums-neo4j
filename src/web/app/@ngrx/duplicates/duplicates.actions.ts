import {createAction} from '@ngrx/store';

export const loadAllDuplicates = createAction(
  '[Duplicates] Load all duplicates'
);

export const loadAllDuplicatesSuccess = createAction(
  '[Duplicates] Load all duplicates success',
  (duplicates: DuplicatesView[]) => ({duplicates})
);
