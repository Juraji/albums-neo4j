import {createAction} from '@ngrx/store';

export const loadFoldersTree = createAction(
  '[Folders] Load folders tree'
);

export const loadFoldersTreeSuccess = createAction(
  '[Folders] Load folders tree success',
  (tree: FolderTreeView[]) => ({tree})
);

export const createFolder = createAction(
  '[Folders] Create folder',
  (folder: Folder, parentId?: string) => ({parentId, folder})
);

export const createFolderSuccess = createAction(
  '[Folders] Create folder success',
  (folder: Folder, parentId?: string) => ({folder, parentId})
);

export const updateFolder = createAction(
  '[Folders] Update folder',
  (folder: Folder) => ({folder})
);

export const updateFolderSuccess = createAction(
  '[Folders] Update folder success',
  (folder: Folder) => ({folder})
);

export const deleteFolder = createAction(
  '[Folders] Delete folder',
  (folderId: string, recursive: boolean) => ({folderId, recursive})
);

export const deleteFolderSuccess = createAction(
  '[Folders] Delete folder success',
  (folderId: string, recursive: boolean) => ({folderId, recursive})
);

export const moveFolder = createAction(
  '[Folders] Move folder',
  (folderId: string, targetId: string) => ({folderId, targetId})
);

export const moveFolderSuccess = createAction(
  '[Folders] Move folder success',
  (folderId: string, targetId: string) => ({folderId, targetId})
);
