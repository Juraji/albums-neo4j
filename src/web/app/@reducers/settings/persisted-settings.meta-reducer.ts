import {INIT, MetaReducer} from '@ngrx/store';

const LS_PREFIX = 'albums-settings--';
const LS_PREFIX_LEN = LS_PREFIX.length;

const getSettingsFromStorage = (): SettingsSliceState => Object
  .entries(localStorage)
  .filter(([key]) => key.startsWith(LS_PREFIX))
  .map(([key, value]) => ({[key.substr(LS_PREFIX_LEN)]: JSON.parse(value)}))
  .reduce((acc, next) => acc.copy(next), {});

const saveSettingsToStorage = (settings: SettingsSliceState) => Object
  .entries(settings)
  .map(([key, value]) => [LS_PREFIX + key, value])
  .forEach(([key, value]) => localStorage.setItem(key, JSON.stringify(value)));

export const persistedSettingsMetaReducer: MetaReducer<AppState> = reducer => (state, action) => {
  if (action.type === INIT) {
    const appState = state || {} as AppState;

    const settings = getSettingsFromStorage();
    return appState.copy({settings});
  }

  const nextState = reducer(state, action);
  saveSettingsToStorage(nextState.settings);
  return nextState;
};
