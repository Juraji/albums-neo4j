import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {createTagSuccess, deleteTagSuccess, loadAllTagsSuccess, updateTagSuccess} from '@actions/tags.actions';

const initialState: TagsSliceState = {
  tags: {},
  tagsLoaded: false
};

export const reducer = createReducer(
  initialState,
  on(loadAllTagsSuccess, (s, {tags}) => {
    const mergeMap = tags.reduce((acc, t) => ({...acc, [t.id]: t}), {});
    return s.copy({tags: mergeMap, tagsLoaded: true});
  }),
  on(createTagSuccess, (s, tag) => s.copy({tags: s.tags.copy({[tag.id]: tag})})),
  on(updateTagSuccess, (s, tag) => s.copy({tags: s.tags.copy({[tag.id]: tag})})),
  on(deleteTagSuccess, (s, tag) => s.copy({tags: s.tags.copy({[tag.id]: undefined})})),
);

export const selectTagsSlice = createFeatureSelector<TagsSliceState>('tags');
export const selectAllTags = createSelector(selectTagsSlice, (s) => Object.values(s.tags));
export const selectTagsLoaded = createSelector(selectTagsSlice, s => s.tagsLoaded);
