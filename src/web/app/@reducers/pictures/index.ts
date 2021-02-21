import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {
  addTagToPictureSuccess,
  loadPicturesSuccess,
  removeTagFromPictureSuccess,
  setAllPicturesLoaded
} from '@actions/pictures.actions';


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
  }),
  on(setAllPicturesLoaded, (s, {directoryId}) => {
    const pdState = s[directoryId] || initialPictureDirectoryState.copy();
    return s.copy({[directoryId]: pdState.copy({fullyLoaded: true})});
  }),
  on(addTagToPictureSuccess, (s, {picture, tag}) => {
    const pdState = s[picture.directory.id] || initialPictureDirectoryState.copy();
    return s.copy({
      [picture.directory.id]: pdState.copy({
        pictures: pdState.pictures
          .map((p) => p.id === picture.id ? p.copy({tags: [...p.tags, tag]}) : p)
      })
    });
  }),
  on(removeTagFromPictureSuccess, (s, {picture, tag}) => {
    const pdState = s[picture.directory.id] || initialPictureDirectoryState.copy();
    return s.copy({
      [picture.directory.id]: pdState.copy({
        pictures: pdState.pictures
          .map((p) => p.id === picture.id ? p.copy({tags: p.tags.filter((t) => t.id !== tag.id)}) : p)
      })
    });
  })
);

export const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');

export const selectPictureById = createSelector(selectPicturesSlice, (s: PicturesSliceState, {pictureId}: SelectPictureByIdProps) =>
  Object.keys(s).flatMap((k) => s[k].pictures).first(p => p.id === pictureId));

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
    return s.pictures.slice(0, end);
  }
);
export const selectLoadedPictureCount = createSelector(selectPictureSet,
  (s) => s.pictures.length);
export const isPictureSetFullyLoaded = createSelector(selectPictureSet,
  (s) => s.fullyLoaded);
