import {createAction} from '@ngrx/store';

export const updateSetting = createAction(
  '[Settings] Update setting',
  (key: string, value: any) => ({key, value})
);
