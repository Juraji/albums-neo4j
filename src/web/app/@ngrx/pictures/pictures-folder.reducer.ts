import {createReducer, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {addPictureSuccess, loadPicturesByFolderIdSuccess, movePicture} from './pictures.actions';

const picturesFolderEntityAdapter = createEntityAdapter<PicturesFolder>({
  selectId: e => e.folderId
});
export const picturesFolderEntitySelectors = picturesFolderEntityAdapter.getSelectors();

// noinspection DuplicatedCode
export const picturesFolderReducer = createReducer(
  picturesFolderEntityAdapter.getInitialState(),
  on(loadPicturesByFolderIdSuccess, (s, {pictures, folderId}) => {
    const update: PicturesFolder = {folderId, pictureIds: pictures.map(p => p.id)};
    return picturesFolderEntityAdapter.upsertOne(update, s);
  }),
  on(addPictureSuccess, (s, {picture, parentFolderId}) => picturesFolderEntityAdapter.mapOne({
    id: parentFolderId,
    map: f => f.copy({pictureIds: f.pictureIds.concat(picture.id)})
  }, s)),
  on(movePicture, (s, {pictureId, targetFolderId}) => {
    let mutation = s;

    const oldFolder = picturesFolderEntitySelectors.selectAll(s).find(e => e.pictureIds.includes(pictureId));
    if (!!oldFolder) {
      mutation = picturesFolderEntityAdapter.mapOne({
        id: oldFolder.folderId,
        map: f => f.copy({pictureIds: f.pictureIds.filter(i => i !== pictureId)})
      }, mutation);
    }

    mutation = picturesFolderEntityAdapter.mapOne({
      id: targetFolderId,
      map: f => f.copy({pictureIds: f.pictureIds.concat(pictureId)})
    }, mutation);

    return mutation;
  })
);
