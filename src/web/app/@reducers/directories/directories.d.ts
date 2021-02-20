interface DirectoriesSliceState {
  tree: Directory[];
  directories: Record<string, Directory>;
}

interface SelectDirectoryProps{
  directoryId: string;
}
