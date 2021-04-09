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
const selectAllFolders = createSelector(selectFolderEntitiesSlice, folderEntitySelectors.selectAll);
const selectTreeMappingEntities = createSelector(selectTreeMappingSlice, folderTreeMappingSelectors.selectEntities);
const selectAllTreeMappings = createSelector(selectTreeMappingSlice, folderTreeMappingSelectors.selectAll);

const selectRootTreeMappings = createSelector(selectAllTreeMappings, s => s.filter(m => m.isRoot));
const selectTreeMappingByFolderId = createSelector(
  selectTreeMappingEntities,
  (s: Dictionary<FolderTreeMapping>, {folderId}: FolderByIdProps) => s[folderId]
);

export const selectRootFolders = createSelector(
  selectRootTreeMappings,
  selectAllFolders,
  (mappings: FolderTreeMapping[], folders: Folder[]) => mappings
    .map(m => folders.find(f => f.id === m.folderId))
);
export const selectFolderById = createSelector(
  selectFoldersEntities,
  (s: Dictionary<Folder>, {folderId}: FolderByIdProps) => s[folderId],
);

export const selectFolderChildrenById = createSelector(
  selectTreeMappingByFolderId,
  selectAllFolders,
  (mapping: FolderTreeMapping | undefined, folders: Folder[]) => (mapping?.children || [])
    .map(fid => folders.find(f => f.id === fid))
);
