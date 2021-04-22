import {createReducer, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {
  createTagSuccess,
  deleteTag,
  loadTagsByPictureIdSuccess,
  loadTagsSuccess,
  updateTag
} from './tags.actions';

const tagsEntityAdapter = createEntityAdapter<Tag>();
export const tagEntitySelectors = tagsEntityAdapter.getSelectors();

export const tagEntitiesReducer = createReducer(
  tagsEntityAdapter.getInitialState(),
  on(loadTagsSuccess, (s, {tags}) => {
    let mutation = s;
    mutation = tagsEntityAdapter.removeAll(mutation);
    mutation = tagsEntityAdapter.addMany(tags, mutation);
    return mutation;
  }),
  on(loadTagsByPictureIdSuccess, (s, {tags}) => tagsEntityAdapter.upsertMany(tags, s)),
  on(createTagSuccess, (s, {tag}) => tagsEntityAdapter.addOne(tag, s)),
  on(updateTag, (s, {tag}) => tagsEntityAdapter.upsertOne(tag, s)),
  on(deleteTag, (s, {tag}) => tagsEntityAdapter.removeOne(tag.id, s))
);
