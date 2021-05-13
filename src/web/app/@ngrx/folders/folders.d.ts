interface FoldersSliceState {
  entities: EntityState<Folder>;
  treeMapping: EntityState<FolderTreeMapping>;
}

interface FolderTreeMapping {
  folderId: string;
  name: string;
  isRoot: boolean;
  children: string[];
}

interface FolderByIdProps {
  folderId: string;
}

interface FolderBySubPath {
  startAtFolderId: string;
  path: string | string[];
}
