import {combineReducers, createFeatureSelector, createSelector} from '@ngrx/store';
import {picturesEntityReducer} from './pictures-entity.reducer';
import {picturesFolderReducer} from './pictures-folder.reducer';

export const reducer = combineReducers<PicturesSliceState>({
  pictures: picturesEntityReducer,
  pictureFolders: picturesFolderReducer,
});

const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');

const selectPictures = createSelector(selectPicturesSlice, s => s.pictures);
const selectPicturesFolders = createSelector(selectPicturesSlice, s => s.pictureFolders);
