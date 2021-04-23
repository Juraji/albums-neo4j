import {createEntityAdapter} from '@ngrx/entity';
import {createReducer, on} from '@ngrx/store';
import {
  addPictureSuccess,
  deletePicture,
  loadPictureByIdSuccess,
  loadPicturesByFolderIdSuccess
} from './pictures.actions';

const picturesEntityAdapter = createEntityAdapter<Picture>();

export const picturesEntitySelectors = picturesEntityAdapter.getSelectors();

export const picturesEntityReducer = createReducer(
  picturesEntityAdapter.getInitialState(),
  on(loadPictureByIdSuccess, (s, {picture}) => picturesEntityAdapter.upsertOne(picture, s)),
  on(loadPicturesByFolderIdSuccess, (s, {pictures}) => picturesEntityAdapter.upsertMany(pictures, s)),
  on(addPictureSuccess, (s, {picture}) => picturesEntityAdapter.upsertOne(picture, s)),
  on(deletePicture, (s, {pictureId}) => picturesEntityAdapter.removeOne(pictureId, s))
);
