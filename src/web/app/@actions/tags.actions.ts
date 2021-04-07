import {createAction} from '@ngrx/store';


export const loadTags = createAction(
  '[Tags] Load tags'
);

export const loadTagsSuccess = createAction(
  '[Tags] Load tags success',
  (tags: Tag[]) => ({tags})
);

export const createTag = createAction(
  '[Tags] Create tag',
  (tag: Tag) => ({tag})
);

export const createTagSuccess = createAction(
  '[Tags] Create tag success',
  (tag: Tag) => ({tag})
);

export const updateTag = createAction(
  '[Tags] Update tag',
  (tag: Tag) => ({tag})
);

export const updateTagSuccess = createAction(
  '[Tags] Update tag success',
  (tag: Tag) => ({tag})
);

export const deleteTag = createAction(
  '[Tags] Delete tag',
  (tag: Tag) => ({tag})
);

export const deleteTagSuccess = createAction(
  '[Tags] Delete tag success',
  (tag: Tag) => ({tag})
);
