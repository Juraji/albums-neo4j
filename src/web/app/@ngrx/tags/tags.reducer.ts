import {combineReducers, createFeatureSelector, createSelector} from '@ngrx/store';
import {tagEntitiesReducer, tagEntitySelectors} from './tag-entities.reducer';
import {pictureTagsEntitySelectors, pictureTagsReducer} from './picture-tags.reducer';
import {Dictionary} from '@ngrx/entity';

export const reducer = combineReducers<TagsSliceState>({
  tags: tagEntitiesReducer,
  pictureTags: pictureTagsReducer
});

const selectTagsSlice = createFeatureSelector<TagsSliceState>('tags');

const selectTags = createSelector(selectTagsSlice, s => s.tags);
const selectPictureTags = createSelector(selectTagsSlice, s => s.pictureTags);

const selectPictureTagEntities = createSelector(selectPictureTags, pictureTagsEntitySelectors.selectEntities);

export const selectTagCount = createSelector(selectTags, tagEntitySelectors.selectTotal);

export const selectAllTags = createSelector(selectTags, tagEntitySelectors.selectAll);

const selectPictureTagsByPictureId = createSelector(
  selectPictureTagEntities,
  (s: Dictionary<PictureTags>, {pictureId}: ByPictureIdProps) =>
    s[pictureId] || {pictureId, tagIds: []}
);

export const selectTagsByPictureId = createSelector(
  selectPictureTagsByPictureId,
  selectAllTags,
  (s: PictureTags, s2: Tag[]) =>
    s.tagIds.map(id => s2.find(t => t.id === id)).filterEmpty()
);
