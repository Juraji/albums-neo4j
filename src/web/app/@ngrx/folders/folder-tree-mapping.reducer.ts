import {createEntityAdapter} from '@ngrx/entity';
import {createReducer, on} from '@ngrx/store';
import {createFolderSuccess, deleteFolder, loadFoldersTreeSuccess, moveFolder} from './folders.actions';

const folderTreeMappingAdapter = createEntityAdapter<FolderTreeMapping>({
  selectId: m => m.folderId
});
export const folderTreeMappingSelectors = folderTreeMappingAdapter.getSelectors();

// noinspection DuplicatedCode
export const folderTreeMappingReducer = createReducer(
  folderTreeMappingAdapter.getInitialState(),
  on(loadFoldersTreeSuccess, (s, {tree}) => {
    const flatten = (fs: FolderTreeView[]): FolderTreeView[] =>
      fs.flatMap(ftv => [ftv, ...flatten(ftv.children)]);

    const folders: FolderTreeMapping[] = flatten(tree).map(ftv => ({
      folderId: ftv.id,
      name: ftv.name,
      isRoot: ftv.isRoot,
      children: ftv.children.map(f => f.id)
    }));

    const emptyState = folderTreeMappingAdapter.removeAll(s);
    return folderTreeMappingAdapter.upsertMany(folders, emptyState);
  }),
  on(createFolderSuccess, (s, {folder, parentId}) => {
    let mutation = s;

    if (!!parentId) {
      mutation = folderTreeMappingAdapter.mapOne({
        id: parentId,
        map: f => f.copy({children: f.children.concat(folder.id)})
      }, mutation);
    }

    mutation = folderTreeMappingAdapter.addOne({
      folderId: folder.id,
      name: folder.name,
      isRoot: !parentId,
      children: []
    }, mutation);

    return mutation;
  }),
  on(deleteFolder, (s, {folderId}) => folderTreeMappingAdapter.removeOne(folderId, s)),
  on(moveFolder, (s, {folderId, targetId}) => {
    let mutation = s;

    const prevParentMapping = folderTreeMappingSelectors.selectAll(s)
      .find(mapping => mapping.children.includes(folderId));

    if (!!prevParentMapping) {
      mutation = folderTreeMappingAdapter.mapOne({
        id: prevParentMapping.folderId,
        map: m => m.copy({children: m.children.filter(fid => fid !== folderId)})
      }, mutation);
    }

    mutation = folderTreeMappingAdapter.mapOne({
      id: targetId,
      map: m => m.copy({children: m.children.concat(folderId)})
    }, mutation);

    return mutation;
  }),
);
