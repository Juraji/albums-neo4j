import {createAction} from '@ngrx/store';

export const foldersTogglePictureSelection = createAction(
  '[Folders route] Toggle picture selection',
  (picture: Picture) => ({picture})
);

export const foldersClearPictureSelection = createAction(
  '[Folders route] Clear picture selection'
);
