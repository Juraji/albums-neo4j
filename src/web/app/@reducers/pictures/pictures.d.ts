type PicturesSliceState = Record<string, PictureDirectoryState>;

interface PictureDirectoryState {
  pictures: PictureProps[];
  fullyLoaded: boolean;
}

interface SelectPicturesSetProps {
  directoryId: string;
}

interface SelectPictureRangeProps extends SelectPicturesSetProps {
  page: number;
  size: number;
}

interface SelectPictureByIdProps {
  pictureId: string;
}

interface FetchPictureProps {
  pictureId: string;
}

interface AddTagToPictureProps {
  picture: PictureProps;
  tag: Tag;
}
