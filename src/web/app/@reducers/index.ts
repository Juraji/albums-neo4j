import {ActionReducerMap, MetaReducer} from '@ngrx/store';
import {environment} from '@environment';
import {reducer as directoriesReducer} from './directories';
import {reducer as picturesReducer} from './pictures';
import {reducer as tagsReducer} from './tags';


export const reducers: ActionReducerMap<AppState> = {
  directories: directoriesReducer,
  pictures: picturesReducer,
  tags: tagsReducer,
};


export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [] : [];
