import {createAction, props} from '@ngrx/store';

export const loadAllTags = createAction(
  '[Tags] Load all tags'
);

export const loadAllTagsSuccess = createAction(
  '[Tags] Load all tags success',
  props<LoadAllTagsSuccessProps>()
);

export const createTag = createAction(
  '[Tags] Create tag',
  props<NewTagDto>()
);

export const createTagSuccess = createAction(
  '[Tags] Create tag success',
  props<Tag>()
);
