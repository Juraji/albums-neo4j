import {createFeatureSelector, createReducer, createSelector} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';

const tagsEntityAdapter = createEntityAdapter<Tag>();

export const reducer = createReducer(
  tagsEntityAdapter.getInitialState()
);

const tagEntitySelectors = tagsEntityAdapter.getSelectors();
const selectTagsSlice = createFeatureSelector<TagsSliceState>('tags');

export const selectAllTags = createSelector(selectTagsSlice, s => tagEntitySelectors.selectAll(s));
