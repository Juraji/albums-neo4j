import {combineReducers, createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {
  fetchAllDuplicatesSuccess,
  scanDuplicatesForPictureSuccess,
  unlinkDuplicateSuccess
} from '@actions/duplicates.actions';

const duplicateEntityAdapter = createEntityAdapter<DuplicateProps>();

const duplicatesReducer = createReducer(
  duplicateEntityAdapter.getInitialState(),
  on(fetchAllDuplicatesSuccess, (s, {duplicates}) => duplicateEntityAdapter.upsertMany(duplicates, s)),
  on(scanDuplicatesForPictureSuccess, (s, {duplicates}) => duplicateEntityAdapter.upsertMany(duplicates, s)),
  on(unlinkDuplicateSuccess, (s, {duplicateId}) => duplicateEntityAdapter.removeOne(duplicateId, s))
);

export const reducer = combineReducers<DuplicatesSliceState>({
  duplicates: duplicatesReducer
});

const selectDuplicatesSlice = createFeatureSelector<DuplicatesSliceState>('duplicates');
const selectDuplicateEntityState = createSelector(selectDuplicatesSlice, s => s.duplicates);

export const selectAllDuplicates = createSelector(
  selectDuplicateEntityState,
  s => Object.values(s.entities).filterEmpty()
);

export const selectDuplicatesByPicture = (pictureId: string) => createSelector(
  selectAllDuplicates,
  entities => entities.filter(e => e.sourceId === pictureId)
);
