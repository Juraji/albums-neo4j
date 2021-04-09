interface FoldersSliceState {
  entities: EntityState<Folder>;
  treeMapping: EntityState<FolderTreeMapping>;
}

interface FolderTreeMapping {
  folderId: string;
  isRoot: boolean;
  children: string[];
}

interface FolderByIdProps {
  folderId: string;
}
