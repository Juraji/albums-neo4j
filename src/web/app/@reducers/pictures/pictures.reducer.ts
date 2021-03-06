import {createEntityAdapter, Update} from '@ngrx/entity';
import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {
  addTagToPictureSuccess,
  fetchDirectoryPicturesSuccess,
  fetchPictureSuccess,
  removeTagFromPictureSuccess
} from '@actions/pictures.actions';
import {deleteTagSuccess, updateTagSuccess} from '@actions/tags.actions';

const picturesEntityAdapter = createEntityAdapter<PictureProps>();
const pictureSelectors = picturesEntityAdapter.getSelectors();

export const picturesReducer = createReducer(
  picturesEntityAdapter.getInitialState(),
  on(fetchPictureSuccess, (s, {picture}) =>
    picturesEntityAdapter.upsertOne(picture, s)),
  on(fetchDirectoryPicturesSuccess, (s, {pictures}) =>
    picturesEntityAdapter.upsertMany(pictures, s)),
  on(addTagToPictureSuccess, (s, {picture}) =>
    picturesEntityAdapter.updateOne({id: picture.id, changes: picture}, s)),
  on(removeTagFromPictureSuccess, (s, {picture}) =>
    picturesEntityAdapter.updateOne({id: picture.id, changes: picture}, s)),
  on(updateTagSuccess, (s, {tag}) => {
    const updatedPictures: Update<PictureProps>[] = pictureSelectors
      .selectAll(s)
      .filter(({tags}) => tags.some(t => t.id === tag.id))
      .map(p => {
        const tagIdx = p.tags.findIndex(t => t.id === tag.id);
        return p.copy({tags: p.tags.replace(tagIdx, tag)});
      })
      .map(({id, ...changes}) => ({id, changes}));

    return picturesEntityAdapter.updateMany(updatedPictures, s);
  }),
  on(deleteTagSuccess, (s, {tag: {id: tagId}}) => {
    const updatedPictures: Update<PictureProps>[] = pictureSelectors
      .selectAll(s)
      .filter(({tags}) => tags.some(t => t.id === tagId))
      .map(p => p.copy({tags: p.tags.filter(t => t.id !== tagId)}))
      .map(({id, ...changes}) => ({id, changes}));

    return picturesEntityAdapter.updateMany(updatedPictures, s);
  }),
);

const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');
const selectPicturesEntityState = createSelector(selectPicturesSlice, s => s.pictures as EntityState<PictureProps>);

const selectPictureEntities = createSelector(selectPicturesEntityState, pictureSelectors.selectEntities);

export const selectPictureById = (id: string) => createSelector(
  selectPictureEntities,
  entities => entities[id]
);

export const selectDirectoryPictures = (directoryId: string) => createSelector(
  selectPictureEntities,
  entities => Object.values(entities)
    .filterEmpty()
    .filter(p => p?.directory?.id === directoryId)
);

export const selectDirectoryPicturesRange = (directoryId: string, page: number, size: number) => createSelector(
  selectDirectoryPictures(directoryId),
  directoryPictures => {
    const start = (page - 1) * size;
    const end = page * size;
    return directoryPictures.slice(start, end);
  }
);
