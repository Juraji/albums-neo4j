import {createEntityAdapter} from '@ngrx/entity';
import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {loadAllDuplicatesSuccess} from './duplicates.actions';

const duplicatesEntityAdapter = createEntityAdapter<DuplicatesView>({
  sortComparer: (a, b) => a.source.id.localeCompare(b.source.id)
});

export const duplicatesEntitySelectors = duplicatesEntityAdapter.getSelectors();

export const reducer = createReducer(
  duplicatesEntityAdapter.getInitialState(),
  on(loadAllDuplicatesSuccess, (s, {duplicates}) => duplicatesEntityAdapter.upsertMany(duplicates, s))
);

const selectDuplicatesSlice = createFeatureSelector<DuplicatesSliceState>('duplicates');

export const selectAllDuplicates = createSelector(
  selectDuplicatesSlice,
  duplicatesEntitySelectors.selectAll
);

export const selectDuplicatesByPictureId = createSelector(
  selectAllDuplicates,
  (s: DuplicatesView[], {pictureId}: DuplicatesByPictureIdProps) => s.filter(d => d.source.id === pictureId)
);
