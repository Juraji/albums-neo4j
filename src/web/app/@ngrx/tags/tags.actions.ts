import {createAction} from '@ngrx/store';


export const loadTags = createAction(
  '[Tags] Load tags'
);

export const loadTagsSuccess = createAction(
  '[Tags] Load tags success',
  (tags: Tag[]) => ({tags})
);

export const loadTagsByPictureId = createAction(
  '[Tags] Load tags by picture',
  (pictureId: string) => ({pictureId})
);

export const loadTagsByPictureIdSuccess = createAction(
  '[Tags] Load tags by picture success',
  (pictureId: string, tags: Tag[]) => ({pictureId, tags})
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

export const addTagToPicture = createAction(
  '[Tag] Add tag to picture',
  (pictureId: string, tag: Tag) => ({pictureId, tag})
);


export const addTagToPictureSuccess = createAction(
  '[Tag] Add tag to picture succes',
  (pictureId: string, tag: Tag) => ({pictureId, tag})
);

export const removeTagFromPicture = createAction(
  '[Tag] Remove tag from picture',
  (pictureId: string, tagId: string) => ({pictureId, tagId})
);


export const removeTagFromPictureSuccess = createAction(
  '[Tag] Remove tag from picture success',
  (pictureId: string, tagId: string) => ({pictureId, tagId})
);
