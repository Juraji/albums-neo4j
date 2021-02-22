import {combineReducers, createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {createTagSuccess, deleteTagSuccess, loadAllTagsSuccess, updateTagSuccess} from '@actions/tags.actions';
import {createEntityAdapter} from '@ngrx/entity';

const tagEntityAdapter = createEntityAdapter<Tag>();

const tagsReducer = createReducer(
  tagEntityAdapter.getInitialState(),
  on(loadAllTagsSuccess, (s, {tags}) => tagEntityAdapter.addMany(tags, s)),
  on(createTagSuccess, (s, {tag}) => tagEntityAdapter.addOne(tag, s)),
  on(updateTagSuccess, (s, {tag}) => tagEntityAdapter.updateOne({id: tag.id, changes: tag}, s)),
  on(deleteTagSuccess, (s, {tag}) => tagEntityAdapter.removeOne(tag.id, s)),
);

const tagsLoadedReducer = createReducer<boolean>(
  false,
  on(loadAllTagsSuccess, (_s) => true)
);

export const reducer = combineReducers<TagsSliceState>({
  tags: tagsReducer,
  tagsLoaded: tagsLoadedReducer
});

const selectTagsSlice = createFeatureSelector<TagsSliceState>('tags');
const selectTagEntityState = createSelector(selectTagsSlice, s => s.tags);

export const selectTagsLoaded = createSelector(selectTagsSlice, s => s.tagsLoaded);

export const selectAllTags = createSelector(
  selectTagEntityState,
  (s) => Object.values(s.entities)
    .filterEmpty()
);
