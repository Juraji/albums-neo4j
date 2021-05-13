interface Folder {
  id: string;
  name: string;
}

interface FolderTreeView extends Folder {
  children: FolderTreeView[];
  isRoot: boolean;
}

interface CreateFolderPDto {
  path: string;
}
