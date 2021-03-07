import {ActionReducerMap, MetaReducer} from '@ngrx/store';
import {reducer as directoriesReducer} from './directories';
import {reducer as picturesReducer} from './pictures';
import {reducer as tagsReducer} from './tags';
import {reducer as duplicatesReducer} from './duplicates';
import {persistedSettingsMetaReducer, reducer as settingsReducer} from './settings';


export const reducers: ActionReducerMap<AppState> = {
  directories: directoriesReducer,
  pictures: picturesReducer,
  tags: tagsReducer,
  duplicates: duplicatesReducer,
  settings: settingsReducer,
};


export const metaReducers: MetaReducer<AppState>[] = [
  persistedSettingsMetaReducer
];
