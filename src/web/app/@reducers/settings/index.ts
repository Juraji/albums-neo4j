import {createFeatureSelector, createReducer, createSelector, on, Selector} from '@ngrx/store';
import {updateSetting} from '@actions/settings.actions';

export const reducer = createReducer<SettingsSliceState>(
  {},
  on(updateSetting, (s, {key, value}) => s.copy({[key]: value}))
);

const selectSettingsSlice = createFeatureSelector<SettingsSliceState>('settings');

export const selectSetting = <R>(key: string, defaultValue: R): Selector<any, R> => createSelector(
  selectSettingsSlice,
  (records) => records.hasOwnProperty(key) ? records[key] : defaultValue
);

export * from './persisted-settings.meta-reducer';
