import {ActionReducerMap, MetaReducer} from '@ngrx/store';
import {environment} from '@environment';
import {reducer as directoriesReducer} from "./directories";


export const reducers: ActionReducerMap<AppState> = {
  directories: directoriesReducer
};


export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [] : [];
