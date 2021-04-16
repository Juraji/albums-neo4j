import {createEntityAdapter} from '@ngrx/entity';
import {createReducer, on} from '@ngrx/store';
import {loadPicturesByFolderIdSuccess} from './pictures.actions';

const picturesEntityAdapter = createEntityAdapter<Picture>();

export const picturesEntitySelectors = picturesEntityAdapter.getSelectors();

export const picturesEntityReducer = createReducer(
  picturesEntityAdapter.getInitialState(),
  on(loadPicturesByFolderIdSuccess, (s, {pictures}) => picturesEntityAdapter.upsertMany(pictures, s))
);
