import {createFeatureSelector, createReducer, createSelector, on} from '@ngrx/store';
import {loadFoldersTreeSuccess} from '@actions/folders.actions';
import {createEntityAdapter, Dictionary} from '@ngrx/entity';

const foldersEntityAdapter = createEntityAdapter<FolderTreeView>({
  sortComparer: (a, b) => a.name.localeCompare(b.name)
});
const folderEntitySelectors = foldersEntityAdapter.getSelectors();

export const reducer = createReducer(
  foldersEntityAdapter.getInitialState(),
  on(loadFoldersTreeSuccess, (s, {tree}) => {
    const flatten = (children: FolderTreeView[]): FolderTreeView[] =>
      children.flatMap((ftv) => [ftv, ...flatten(ftv.children)]);

    const folders = flatten(tree);
    const emptyState = foldersEntityAdapter.removeAll(s);
    return foldersEntityAdapter.upsertMany(folders, emptyState);
  })
);

const selectFoldersSlice = createFeatureSelector<FoldersSliceState>('folders');

const selectFoldersEntities = createSelector(selectFoldersSlice, folderEntitySelectors.selectEntities);
const selectAllFolders = createSelector(selectFoldersSlice, folderEntitySelectors.selectAll);

export const selectRootFolders = createSelector(
  selectAllFolders,
  s => s.filter(f => f.isRoot)
);
export const selectFolderById = createSelector(
  selectFoldersEntities,
  (s: Dictionary<FolderTreeView>, {folderId}: FolderByIdProps) => s[folderId],
);

export const selectFolderChildrenById = createSelector(
  selectFolderById,
  f => f?.children || []
);
