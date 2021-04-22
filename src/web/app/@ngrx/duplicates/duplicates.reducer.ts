import {createEntityAdapter} from '@ngrx/entity';
import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {loadAllDuplicatesSuccess, unlinkDuplicate} from './duplicates.actions';
import {deletePicture} from '@ngrx/pictures';

const createIdByIds = (sourceId: string, targetId: string): string =>
  'xxxxxxxx-xxxx-4xxx-xxxx-xxxxxxxxxxxx'.replace(/[x]/g, (c, p) => {
    const c1 = sourceId.charAt(p);
    const c2 = targetId.charAt(p);
    return (parseInt(c1, 16) ^ parseInt(c2, 16)).toString(16).charAt(0);
  });
const createId = (d: DuplicatesView): string => createIdByIds(d.sourceId, d.targetId);

const duplicatesEntityAdapter = createEntityAdapter<DuplicatesView>({
  selectId: d => d.trackingId$,
  sortComparer: (a, b) => a.sourceId.localeCompare(b.sourceId)
});

export const duplicatesEntitySelectors = duplicatesEntityAdapter.getSelectors();

export const reducer = createReducer(
  duplicatesEntityAdapter.getInitialState(),
  on(loadAllDuplicatesSuccess, (s, {duplicates}) => {
    const trackedDuplicates = duplicates.map(d => d.copy({trackingId$: createId(d)}));
    return duplicatesEntityAdapter.upsertMany(trackedDuplicates, s);
  }),
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
    s.filter(d => d.sourceId === pictureId || d.targetId === pictureId)
);
