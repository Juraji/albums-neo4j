import {combineReducers, createFeatureSelector, createSelector} from '@ngrx/store';
import {folderSelectedPicturesReducer, selectedPicturesEntitySelectors} from './folder-selected-pictures.reducer';

export const folderRouteReducer = combineReducers<FolderRouteSliceState>({
  selectedPictures: folderSelectedPicturesReducer
});

const selectFolderRouteSlice = createFeatureSelector<FolderRouteSliceState>('foldersRoute');
const foldersSelectSelectedPictures = createSelector(selectFolderRouteSlice, s => s.selectedPictures);

const foldersSelectSelectedPicturesIds = createSelector(
  foldersSelectSelectedPictures,
  selectedPicturesEntitySelectors.selectIds as (s: any) => string[]
);

export const foldersSelectAllSelectedPictures = createSelector(
  foldersSelectSelectedPictures,
  selectedPicturesEntitySelectors.selectAll
);

export const foldersSelectIsSelectedByPictureId = createSelector(
  foldersSelectSelectedPicturesIds,
  (s: string[], {pictureId}: ByPictureIdProps) => s.includes(pictureId)
);
