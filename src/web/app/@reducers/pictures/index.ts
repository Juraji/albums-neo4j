import {combineReducers} from '@ngrx/store';
import {directoryLoadStatesReducer} from './directory-states.reducer';
import {picturesReducer} from './pictures.reducer';

export const reducer = combineReducers<PicturesSliceState>({
  pictures: picturesReducer,
  directoryLoadStates: directoryLoadStatesReducer
});

export * from './pictures.reducer';
export * from './directory-states.reducer';
