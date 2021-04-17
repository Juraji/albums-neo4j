import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {createTagSuccess, deleteTag, loadTagsSuccess, updateTag} from "@ngrx/tags/tags.actions";

const tagsEntityAdapter = createEntityAdapter<Tag>();

export const reducer = createReducer(
  tagsEntityAdapter.getInitialState(),
  on(loadTagsSuccess, (s, {tags}) => {
    let mutation = s;
    mutation = tagsEntityAdapter.removeAll(mutation);
    mutation = tagsEntityAdapter.addMany(tags, mutation);
    return mutation;
  }),
  on(createTagSuccess, (s, {tag}) => tagsEntityAdapter.addOne(tag, s)),
  on(updateTag, (s, {tag}) => tagsEntityAdapter.upsertOne(tag, s)),
  on(deleteTag, (s, {tag}) => tagsEntityAdapter.removeOne(tag.id, s))
);

const tagEntitySelectors = tagsEntityAdapter.getSelectors();
const selectTagsSlice = createFeatureSelector<TagsSliceState>('tags');

export const selectTagCount = createSelector(
  selectTagsSlice,
  s => tagEntitySelectors.selectIds(s).length
);

export const selectAllTags = createSelector(
  selectTagsSlice,
  s => tagEntitySelectors.selectAll(s)
);
