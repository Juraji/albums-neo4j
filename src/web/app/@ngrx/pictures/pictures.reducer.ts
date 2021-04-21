import {combineReducers, createFeatureSelector, createSelector} from '@ngrx/store';
import {picturesEntityReducer, picturesEntitySelectors} from './pictures-entity.reducer';
import {picturesFolderEntitySelectors, picturesFolderReducer} from './pictures-folder.reducer';
import {Dictionary} from '@ngrx/entity';

export const reducer = combineReducers<PicturesSliceState>({
  pictures: picturesEntityReducer,
  pictureFolders: picturesFolderReducer,
});

const selectPicturesSlice = createFeatureSelector<PicturesSliceState>('pictures');

const selectPictures = createSelector(selectPicturesSlice, s => s.pictures);
const selectPicturesFolders = createSelector(selectPicturesSlice, s => s.pictureFolders);

const selectPictureFolderEntities = createSelector(
  selectPicturesFolders,
  picturesFolderEntitySelectors.selectEntities
);

const selectPictureEntities = createSelector(
  selectPictures,
  picturesEntitySelectors.selectEntities
);

const selectAllPictureEntities = createSelector(
  selectPictures,
  picturesEntitySelectors.selectAll
);

const selectFolderPicturesByFolderId = createSelector(
  selectPictureFolderEntities,
  (s: Dictionary<PicturesFolder>, {folderId}: FolderByIdProps) => s[folderId]
);

export const selectPicturesByFolderId = createSelector(
  selectFolderPicturesByFolderId,
  selectAllPictureEntities,
  (fp, pe) => (fp?.pictureIds || [])
    .map(id => pe.find(p => p.id === id))
    .filterEmpty()
);

export const selectPictureById = createSelector(
  selectPictureEntities,
  (s: Dictionary<Picture>, {pictureId}: ByPictureIdProps) => s[pictureId]
);

export const selectFolderIdBiyPictureId = createSelector(
  selectPictureFolderEntities,
  (s: Dictionary<PicturesFolder>, {pictureId}: ByPictureIdProps) => Object.values(s)
    .filterEmpty()
    .find(pf => pf.pictureIds.includes(pictureId))
    ?.folderId
);

