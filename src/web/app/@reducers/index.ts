import {ActionReducerMap, MetaReducer} from '@ngrx/store';
import {environment} from '@environment';
import {reducer as directoriesReducer, DirectoriesSliceState} from "./directories.reducer";


export interface AppState {
  directories: DirectoriesSliceState
}

export const reducers: ActionReducerMap<AppState> = {
  directories: directoriesReducer
};


export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [] : [];
