import {createAction} from '@ngrx/store';

export const loadAllTags = createAction('[Tags] Load all tags');
export const loadAllTagsSuccess = createAction('[Tags] Load all tags success', (tags: Tag[]) => ({tags}));
export const createTag = createAction('[Tags] Create tag', (newTag: NewTagDto) => ({newTag}));
export const createTagSuccess = createAction('[Tags] Create tag success', (tag: Tag) => ({tag}));
export const updateTag = createAction('[Tags] Update tag', (tag: Tag) => ({tag}));
export const updateTagSuccess = createAction('[Tags] Update tag success', (tag: Tag) => ({tag}));
export const deleteTag = createAction('[Tags] deleteTag', (tag: Tag) => ({tag}));
export const deleteTagSuccess = createAction('[Tags] deleteTag success', (tag: Tag) => ({tag}));
