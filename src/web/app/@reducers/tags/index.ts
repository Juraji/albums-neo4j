import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {createTagSuccess, loadAllTagsSuccess} from '@actions/tags.actions';

const initialState: TagsSliceState = {
  tags: []
};

export const reducer = createReducer(
  initialState,
  on(loadAllTagsSuccess, (s, props) => s.copy(props)),
  on(createTagSuccess, (s, tag) => s.copy({tags: [...s.tags, tag]}))
);

export const selectTagsSlice = createFeatureSelector<TagsSliceState>('tags');
export const selectAllTags = createSelector(selectTagsSlice, (s) => s.tags);
