import {combineReducers} from '@ngrx/store';
import {picturesReducer} from './pictures.reducer';

export const reducer = combineReducers<PicturesSliceState>({
  pictures: picturesReducer,
});

export * from './pictures.reducer';
