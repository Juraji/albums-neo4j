interface Folder {
  id: string;
  name: string;
}

interface FolderTreeView {
  id: string;
  name: string;
  children: FolderTreeView[];
  isRoot: boolean;
}
