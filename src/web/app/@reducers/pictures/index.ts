import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {
  addTagToPictureSuccess,
  fetchDirectoryPicturesSuccess,
  fetchPictureSuccess,
  removeTagFromPictureSuccess,
  setDirectoryLoadState,
} from '@actions/pictures.actions';


const initialState: PicturesSliceState = {
  pictures: {},
  directoryLoadStates: {},
};

export const reducer = createReducer(
  initialState,
  on(fetchPictureSuccess, (s, picture) =>
    s.copy({pictures: s.pictures.copy({[picture.id]: picture})})),
  on(fetchDirectoryPicturesSuccess, (s, {pictures}) => {
    const mergeMap: PictureMap = pictures.reduce((acc, p) => acc.copy({[p.id]: p}), {});
    return s.copy({pictures: s.pictures.copy(mergeMap)});
  }),
  on(addTagToPictureSuccess, (s, picture) =>
    s.copy({pictures: s.pictures.copy({[picture.id]: picture})})),
  on(removeTagFromPictureSuccess, (s, picture) =>
    s.copy({pictures: s.pictures.copy({[picture.id]: picture})})),
  on(setDirectoryLoadState, (s, {directoryId, state}) => {
    const directoryLoadStates = s.directoryLoadStates.copy({[directoryId]: state});
    return s.copy({directoryLoadStates});
  })
);

export const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');
export const selectPicturesMap = createSelector(selectPicturesSlice, s => s.pictures);
export const selectDirectoryLoadStateMap = createSelector(selectPicturesSlice, s => s.directoryLoadStates);

export const selectPictureById = createSelector(
  selectPicturesMap,
  (s: PictureMap, pictureId: string) => s[pictureId]
);

export const selectDirectoryPictures = createSelector(
  selectPicturesMap,
  (s: PictureMap, {directoryId}: SelectDirectoryPicturesProps) => Object.values(s)
    .filter((p) => p.directory.id === directoryId)
);

export const selectDirectoryPicturesRange = createSelector(
  selectDirectoryPictures,
  (s: PictureProps[], {page, size}: SelectDirectoryPicturesRangeProps) => {
    const start = page * size;
    const end = start + size;
    return s.slice(0, end);
  }
);

export const selectDirectoryLoadState = createSelector(
  selectDirectoryLoadStateMap,
  (s: StateMap, directoryId: string) => s[directoryId]
);
