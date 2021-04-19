import {createReducer, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {addPictureSuccess, deletePicture, loadPicturesByFolderIdSuccess, movePicture} from './pictures.actions';
import {deleteFolder} from '@ngrx/folders';

const picturesFolderEntityAdapter = createEntityAdapter<PicturesFolder>({
  selectId: e => e.folderId
});
export const picturesFolderEntitySelectors = picturesFolderEntityAdapter.getSelectors();

const findByPictureId = (s: EntityState<PicturesFolder>, pictureId: string): PicturesFolder | undefined =>
  picturesFolderEntitySelectors.selectAll(s).find(e => e.pictureIds.includes(pictureId));

const addPictureId = (s: EntityState<PicturesFolder>, folderId: string, pictureId: string): EntityState<PicturesFolder> =>
  picturesFolderEntityAdapter.mapOne({
    id: folderId,
    map: f => f.copy({pictureIds: f.pictureIds.concat(pictureId)})
  }, s);

const removePictureId = (s: EntityState<PicturesFolder>, folderId: string, pictureId: string): EntityState<PicturesFolder> =>
  picturesFolderEntityAdapter.mapOne({
    id: folderId,
    map: f => f.copy({pictureIds: f.pictureIds.filter(i => i !== pictureId)})
  }, s);

export const picturesFolderReducer = createReducer(
  picturesFolderEntityAdapter.getInitialState(),
  on(loadPicturesByFolderIdSuccess, (s, {pictures, folderId}) => {
    const update: PicturesFolder = {folderId, pictureIds: pictures.map(p => p.id)};
    return picturesFolderEntityAdapter.upsertOne(update, s);
  }),
  on(deleteFolder, (s, {folderId}) => picturesFolderEntityAdapter.removeOne(folderId, s)),
  on(addPictureSuccess, (s, {picture, parentFolderId}) => picturesFolderEntityAdapter.mapOne({
    id: parentFolderId,
    map: f => f.copy({pictureIds: f.pictureIds.concat(picture.id)})
  }, s)),
  on(movePicture, (s, {pictureId, targetFolderId}) => {
    let mutation = s;

    const oldFolder = findByPictureId(s, pictureId);
    if (!!oldFolder) {
      mutation = removePictureId(mutation, oldFolder.folderId, pictureId);
    }

    mutation = addPictureId(mutation, targetFolderId, pictureId);

    return mutation;
  }),
  on(deletePicture, (s, {pictureId}) => {
    const pFolder = findByPictureId(s, pictureId);
    return !!pFolder ? removePictureId(s, pFolder.folderId, pictureId) : s;
  })
);
