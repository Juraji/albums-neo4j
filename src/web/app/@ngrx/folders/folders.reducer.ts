import {combineReducers, createFeatureSelector, createSelector} from '@ngrx/store';
import {Dictionary} from '@ngrx/entity';
import {folderEntitiesReducer, folderEntitySelectors} from './folder-entities.reducer';
import {folderTreeMappingReducer, folderTreeMappingSelectors} from './folder-tree-mapping.reducer';

export const reducer = combineReducers<FoldersSliceState>({
  entities: folderEntitiesReducer,
  treeMapping: folderTreeMappingReducer
});

const selectFoldersSlice = createFeatureSelector<FoldersSliceState>('folders');
const selectFolderEntitiesSlice = createSelector(selectFoldersSlice, s => s.entities);
const selectTreeMappingSlice = createSelector(selectFoldersSlice, s => s.treeMapping);

const selectFoldersEntities = createSelector(selectFolderEntitiesSlice, folderEntitySelectors.selectEntities);
export const selectAllFolders = createSelector(selectFolderEntitiesSlice, folderEntitySelectors.selectAll);
const selectTreeMappingEntities = createSelector(selectTreeMappingSlice, folderTreeMappingSelectors.selectEntities);
const selectAllTreeMappings = createSelector(selectTreeMappingSlice, folderTreeMappingSelectors.selectAll);

const selectRootTreeMappings = createSelector(selectAllTreeMappings, s => s.filter(m => m.isRoot));
const selectTreeMappingByFolderId = createSelector(
  selectTreeMappingEntities,
  (s: Dictionary<FolderTreeMapping>, {folderId}: FolderByIdProps) => s[folderId]
);

const findFolderById = (folders: Folder[]) => (id: string) => folders.find(f => f.id === id);

export const selectRootFolders = createSelector(
  selectRootTreeMappings,
  selectAllFolders,
  (mappings: FolderTreeMapping[], folders: Folder[]) => mappings
    .map(m => m.folderId)
    .map(findFolderById(folders))
);
export const selectFolderById = createSelector(
  selectFoldersEntities,
  (s: Dictionary<Folder>, {folderId}: FolderByIdProps) => s[folderId],
);

export const selectFolderChildrenById = createSelector(
  selectTreeMappingByFolderId,
  selectAllFolders,
  (mapping: FolderTreeMapping | undefined, folders: Folder[]) => (mapping?.children || [])
    .map(findFolderById(folders))
);

export const selectTreePathByFolderId = createSelector(
  selectAllTreeMappings,
  selectAllFolders,
  (treeMappings: FolderTreeMapping[], folders: Folder[], {folderId}: FolderByIdProps) => {
    const findParentId = (id: string): string | undefined => treeMappings.find(it => it.children.includes(id))?.folderId;

    let path: string[] = [];
    let lastParentId = findParentId(folderId);

    while (lastParentId !== undefined) {
      path = path.concat(lastParentId);
      lastParentId = findParentId(lastParentId);
    }

    return path
      .reverse()
      .concat(folderId)
      .map(findFolderById(folders))
      .filter(it => it !== undefined);
  }
);
