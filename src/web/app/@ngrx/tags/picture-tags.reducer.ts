import {createEntityAdapter} from '@ngrx/entity';
import {createReducer, on} from '@ngrx/store';
import {addTagToPicture, deleteTag, loadTagsByPictureIdSuccess, removeTagFromPicture} from './tags.actions';
import {deletePicture} from '@ngrx/pictures';

const pictureTagsEntityAdapter = createEntityAdapter<PictureTags>({
  selectId: e => e.pictureId
});
export const pictureTagsEntitySelectors = pictureTagsEntityAdapter.getSelectors();

export const pictureTagsReducer = createReducer(
  pictureTagsEntityAdapter.getInitialState(),
  on(loadTagsByPictureIdSuccess, (s, {pictureId, tags}) =>
    pictureTagsEntityAdapter.upsertOne({pictureId, tagIds: tags.map(t => t.id)}, s)),
  on(addTagToPicture, (s, {pictureId, tag}) => pictureTagsEntityAdapter.mapOne({
    id: pictureId,
    map: pt => pt.copy({tagIds: pt.tagIds.concat(tag.id)})
  }, s)),
  on(removeTagFromPicture, (s, {pictureId, tagId}) => pictureTagsEntityAdapter.mapOne({
    id: pictureId,
    map: pt => pt.copy({tagIds: pt.tagIds.filter(id => id !== tagId)})
  }, s)),
  on(deletePicture, (s, {pictureId}) => pictureTagsEntityAdapter.removeOne(pictureId, s)),
  on(deleteTag, (s, {tag}) => {
    const updates = pictureTagsEntitySelectors.selectAll(s)
      .filter(pt => pt.tagIds.includes(tag.id))
      .map(pt => pt.copy({tagIds: pt.tagIds.filter(tid => tid !== tag.id)}));

    return pictureTagsEntityAdapter.upsertMany(updates, s);
  })
);
