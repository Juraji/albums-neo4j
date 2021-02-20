import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {loadPicturesSuccess} from '@actions/pictures.actions';


const initialState: PicturesSliceState = {};

const initialPictureDirectoryState: PictureDirectoryState = {
  pictures: [],
  fullyLoaded: false
};

export const reducer = createReducer(
  initialState,
  on(loadPicturesSuccess, (s, {directoryId, pictures}) => {
    const pdState = s[directoryId] || initialPictureDirectoryState.copy();

    return s.copy({
      [directoryId]: pdState.copy({
        pictures: [...pdState.pictures, ...pictures]
      })
    });
  })
);

export const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');
export const selectPictureSet = createSelector(
  selectPicturesSlice,
  (s: PicturesSliceState, {directoryId}: SelectPicturesSetProps) =>
    s[directoryId] || initialPictureDirectoryState.copy()
);
export const selectPicturesRange = createSelector(
  selectPictureSet,
  (s: PictureDirectoryState, {page, size}: SelectPictureRangeProps) => {
    const start = page * size;
    const end = start + size;
    return s.pictures.slice(start, end);
  }
);
export const selectLoadedPictureCount = createSelector(selectPictureSet,
  (s) => s.pictures.length);
