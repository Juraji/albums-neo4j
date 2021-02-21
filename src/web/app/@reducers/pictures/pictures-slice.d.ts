interface PicturesSliceState {
  pictures: EntityState<PictureProps>;
  directoryLoadStates: Record<string, boolean>;
}

interface FetchPictureProps {
  pictureId: string;
}

interface FetchDirectoryPicturesProps {
  directoryId: string;
  page: number;
  size: number;
}

interface FetchDirectoryPicturesSuccessProps {
  pictures: PictureProps[];
}

interface AddTagToPictureProps {
  picture: PictureProps;
  tag: Tag;
}

interface SetDirectoryLoadStateProps {
  directoryId: string;
  state: boolean;
}
