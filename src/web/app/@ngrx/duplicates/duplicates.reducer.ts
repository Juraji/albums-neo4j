import {createEntityAdapter} from '@ngrx/entity';
import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {loadAllDuplicatesSuccess, unlinkDuplicate} from './duplicates.actions';
import {deletePicture} from '@ngrx/pictures';

const createIdByIds = (sourceId: string, targetId: string): string => `${sourceId}--${targetId}`;
const createId = (d: DuplicatesView): string => createIdByIds(d.sourceId, d.targetId);

const duplicatesEntityAdapter = createEntityAdapter<DuplicatesView>({
  selectId: createId,
  sortComparer: (a, b) => a.sourceId.localeCompare(b.sourceId)
});

export const duplicatesEntitySelectors = duplicatesEntityAdapter.getSelectors();

export const reducer = createReducer(
  duplicatesEntityAdapter.getInitialState(),
  on(loadAllDuplicatesSuccess, (s, {duplicates}) => duplicatesEntityAdapter.upsertMany(duplicates, s)),
  on(unlinkDuplicate, (s, {sourcePictureId, targetPictureId}) => {
    const id = createIdByIds(sourcePictureId, targetPictureId);
    return duplicatesEntityAdapter.removeOne(id, s);
  }),
  on(deletePicture, (s, {pictureId}) => {
    const ids = (duplicatesEntitySelectors.selectIds(s) as string[]).filter(i => i.includes(pictureId));
    return duplicatesEntityAdapter.removeMany(ids, s);
  })
);

const selectDuplicatesSlice = createFeatureSelector<DuplicatesSliceState>('duplicates');

export const selectAllDuplicates = createSelector(
  selectDuplicatesSlice,
  duplicatesEntitySelectors.selectAll
);

export const selectDuplicateCount = createSelector(
  selectDuplicatesSlice,
  duplicatesEntitySelectors.selectTotal,
);

export const selectDuplicatesByPictureId = createSelector(
  selectAllDuplicates,
  (s: DuplicatesView[], {pictureId}: ByPictureIdProps) =>
    s.filter(d => d.sourceId === pictureId)
);
