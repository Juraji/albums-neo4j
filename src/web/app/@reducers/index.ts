import {ActionReducerMap, MetaReducer} from '@ngrx/store';
import {persistedSettingsMetaReducer, reducer as settingsReducer} from './settings';
import {reducer as foldersReducer} from './folders';
import {reducer as picturesReducer} from './pictures';
import {reducer as duplicatesReducer} from './duplicates';
import {reducer as tagsReducer} from './tags';


export const reducers: ActionReducerMap<AppState> = {
  settings: settingsReducer,
  folders: foldersReducer,
  pictures: picturesReducer,
  duplicates: duplicatesReducer,
  tags: tagsReducer
};


export const metaReducers: MetaReducer<AppState>[] = [
  persistedSettingsMetaReducer
];
