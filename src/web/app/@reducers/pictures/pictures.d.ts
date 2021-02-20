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
