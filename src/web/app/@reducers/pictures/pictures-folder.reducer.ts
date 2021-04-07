import {createReducer} from '@ngrx/store';
import {createEntityAdapter} from '@ngrx/entity';

const picturesFolderEntityAdapter = createEntityAdapter<PicturesFolder>({
  selectId: e => e.folderId
});

export const picturesFolderEntitySelectors = picturesFolderEntityAdapter.getSelectors();

export const picturesFolderReducer = createReducer(
  picturesFolderEntityAdapter.getInitialState()
);
