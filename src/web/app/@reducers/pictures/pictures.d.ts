interface PicturesSliceState {
  pictures: PictureMap;
  directoryLoadStates: StateMap;
}

type PictureMap = Record<string, PictureProps>;
type StateMap = Record<string, boolean>;

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

interface SelectDirectoryPicturesProps {
  directoryId: string;
}

interface SelectDirectoryPicturesRangeProps extends SelectDirectoryPicturesProps {
  page: number;
  size: number;
}

interface SetDirectoryLoadStateProps {
  directoryId: string;
  state: boolean;
}
