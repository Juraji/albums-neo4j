import {createReducer, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {foldersClearPictureSelection, foldersTogglePictureSelection} from './folder-route.actions';
import {routerNavigationAction} from '@ngrx/router-store';
import {deletePicture} from '@ngrx/pictures';

const selectedPicturesEntityAdapter = createEntityAdapter<Picture>();
export const selectedPicturesEntitySelectors = selectedPicturesEntityAdapter.getSelectors();

export const folderSelectedPicturesReducer = createReducer(
  selectedPicturesEntityAdapter.getInitialState(),
  on(foldersTogglePictureSelection, (s, {picture}) =>
    (s.ids as string[]).includes(picture.id)
      ? selectedPicturesEntityAdapter.removeOne(picture.id, s)
      : selectedPicturesEntityAdapter.addOne(picture, s)),
  on(foldersClearPictureSelection, selectedPicturesEntityAdapter.removeAll),
  on(routerNavigationAction, selectedPicturesEntityAdapter.removeAll),
  on(deletePicture, (s, {pictureId}) => selectedPicturesEntityAdapter.removeOne(pictureId, s))
);
