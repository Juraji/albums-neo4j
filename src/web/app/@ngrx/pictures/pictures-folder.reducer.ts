import {createReducer, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {
  addPictureSuccess,
  deletePicture,
  loadPictureByIdSuccess,
  loadPicturesByFolderIdSuccess,
  movePicture
} from './pictures.actions';
import {deleteFolder} from '@ngrx/folders';

const picturesFolderEntityAdapter = createEntityAdapter<PicturesFolder>({
  selectId: e => e.folderId
});
export const picturesFolderEntitySelectors = picturesFolderEntityAdapter.getSelectors();

const findByPictureId = (s: EntityState<PicturesFolder>, pictureId: string): PicturesFolder | undefined =>
  picturesFolderEntitySelectors.selectAll(s).find(e => e.pictureIds.includes(pictureId));

const mutatePids = (
  s: EntityState<PicturesFolder>,
  folderId: string,
  mutation: (pids: string[]) => string[]
): EntityState<PicturesFolder> => {
  if ((s.ids as string[]).includes(folderId)) {
    return picturesFolderEntityAdapter.mapOne({
      id: folderId,
      map: f => f.copy({pictureIds: mutation(f.pictureIds)})
    }, s);
  } else {
    return picturesFolderEntityAdapter.upsertOne({folderId, pictureIds: mutation([])}, s);
  }
};

const addPictureId = (s: EntityState<PicturesFolder>, folderId: string, pictureId: string): EntityState<PicturesFolder> =>
  mutatePids(s, folderId, pids => pids.concat(pictureId));

const removePictureId = (s: EntityState<PicturesFolder>, folderId: string, pictureId: string): EntityState<PicturesFolder> =>
  mutatePids(s, folderId, pids => pids.filter(i => i !== pictureId));

export const picturesFolderReducer = createReducer(
  picturesFolderEntityAdapter.getInitialState(),
  on(loadPicturesByFolderIdSuccess, (s, {pictures, folderId}) => mutatePids(s, folderId, () => pictures.map(p => p.id))),
  on(loadPictureByIdSuccess, (s, {picture, folder}) => addPictureId(s, folder.id, picture.id)),
  on(deleteFolder, (s, {folderId}) => picturesFolderEntityAdapter.removeOne(folderId, s)),
  on(addPictureSuccess, (s, {picture, parentFolderId}) => addPictureId(s, parentFolderId, picture.id)),
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
