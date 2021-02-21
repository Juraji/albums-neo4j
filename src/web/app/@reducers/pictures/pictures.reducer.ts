import {createEntityAdapter, EntityAdapter, Update} from '@ngrx/entity';
import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {
  addTagToPictureSuccess,
  fetchDirectoryPicturesSuccess,
  fetchPictureSuccess,
  removeTagFromPictureSuccess
} from '@actions/pictures.actions';
import {deleteTagSuccess} from '@actions/tags.actions';

const picturesEntityAdapter: EntityAdapter<PictureProps> = createEntityAdapter();
const pictureSelectors = picturesEntityAdapter.getSelectors();

export const picturesReducer = createReducer(
  picturesEntityAdapter.getInitialState(),
  on(fetchPictureSuccess, (s, {picture}) =>
    picturesEntityAdapter.addOne(picture, s)),
  on(fetchDirectoryPicturesSuccess, (s, {pictures}) =>
    picturesEntityAdapter.addMany(pictures, s)),
  on(addTagToPictureSuccess, (s, {picture}) =>
    picturesEntityAdapter.updateOne({id: picture.id, changes: picture}, s)),
  on(removeTagFromPictureSuccess, (s, {picture}) =>
    picturesEntityAdapter.updateOne({id: picture.id, changes: picture}, s)),
  on(deleteTagSuccess, (s, {id: tagId}) => {
    const updatedPictures: Update<PictureProps>[] = Object.values(s.entities)
      .filterEmpty()
      .filter(p => p.tags.some(t => t.id === tagId))
      .map(p => p.copy({tags: p.tags.filter(t => t.id !== tagId)}))
      .map(p => ({id: p.id, changes: p}));

    return picturesEntityAdapter.updateMany(updatedPictures, s);
  }),
);

const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');
const selectPicturesEntityState = createSelector(selectPicturesSlice, s => s.pictures as EntityState<PictureProps>);

const selectPictureEntities = createSelector(
  selectPicturesEntityState,
  pictureSelectors.selectEntities
);

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
    const start = page * size;
    const end = start + size;
    return directoryPictures.slice(0, end);
  }
);
