import {createEntityAdapter} from '@ngrx/entity';
import {createReducer, on} from '@ngrx/store';
import {createFolderSuccess, deleteFolder, loadFoldersTreeSuccess, updateFolder} from './folders.actions';

const foldersEntityAdapter = createEntityAdapter<Folder>({
  sortComparer: (a, b) => a.name.localeCompare(b.name)
});
export const folderEntitySelectors = foldersEntityAdapter.getSelectors();

export const folderEntitiesReducer = createReducer(
  foldersEntityAdapter.getInitialState(),
  on(loadFoldersTreeSuccess, (s, {tree}) => {
    const flatten = (fs: FolderTreeView[]): Folder[] =>
      fs.flatMap(({children, isRoot, ...props}) => [props, ...flatten(children)]);

    const folders = flatten(tree);
    const emptyState = foldersEntityAdapter.removeAll(s);
    return foldersEntityAdapter.upsertMany(folders, emptyState);
  }),
  on(createFolderSuccess, (s, {folder}) => foldersEntityAdapter.addOne(folder, s)),
  on(updateFolder, (s, {folder}) => foldersEntityAdapter.updateOne({id: folder.id, changes: folder}, s)),
  on(deleteFolder, (s, {folderId}) => foldersEntityAdapter.removeOne(folderId, s)),
);
