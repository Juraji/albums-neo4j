import {ActionReducerMap, MetaReducer} from '@ngrx/store';
import {persistedSettingsMetaReducer, reducer as settingsReducer} from './settings';
import {FoldersEffects, reducer as foldersReducer} from './folders';
import {PicturesEffects, reducer as picturesReducer} from './pictures';
import {DuplicatesEffects, reducer as duplicatesReducer} from './duplicates';
import {reducer as tagsReducer, TagsEffects} from './tags';

export const ROOT_REDUCER: ActionReducerMap<AppState> = {
  settings: settingsReducer,
  folders: foldersReducer,
  pictures: picturesReducer,
  duplicates: duplicatesReducer,
  tags: tagsReducer,
};


export const ROOT_META_REDUCERS: MetaReducer<AppState>[] = [
  persistedSettingsMetaReducer
];

export const ROOT_EFFECTS = [FoldersEffects, PicturesEffects, TagsEffects, DuplicatesEffects];
