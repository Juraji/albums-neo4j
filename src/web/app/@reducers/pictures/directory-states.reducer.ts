import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {setDirectoryLoadState} from '@actions/pictures.actions';

export const directoryLoadStatesReducer = createReducer(
  {},
  on(setDirectoryLoadState, (s, {directoryId, state}) => s.copy({[directoryId]: state})),
);

const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');
const selectDirectoryLoadStateMap = createSelector(selectPicturesSlice, s => s.directoryLoadStates);

export const selectDirectoryLoadState = (directoryId: string) => createSelector(
  selectDirectoryLoadStateMap,
  states => states[directoryId] || false
);
