import {ActionReducerMap, MetaReducer} from '@ngrx/store';
import {environment} from '@environment';
import {reducer as directoriesReducer} from "./directories";
import {reducer as picturesReducer} from "./pictures";


export const reducers: ActionReducerMap<AppState> = {
  directories: directoriesReducer,
  pictures: picturesReducer
};


export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [] : [];
