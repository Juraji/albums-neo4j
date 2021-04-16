import {createReducer, on} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';
import {loadPicturesByFolderIdSuccess} from './pictures.actions';

const picturesFolderEntityAdapter = createEntityAdapter<PicturesFolder>({
  selectId: e => e.folderId
});
export const picturesFolderEntitySelectors = picturesFolderEntityAdapter.getSelectors();

export const picturesFolderReducer = createReducer(
  picturesFolderEntityAdapter.getInitialState(),
  on(loadPicturesByFolderIdSuccess, (s, {pictures, folderId}) => {
    const update: PicturesFolder = {folderId, pictureIds: pictures.map(p => p.id)};
    return picturesFolderEntityAdapter.upsertOne(update, s);
  })
);
