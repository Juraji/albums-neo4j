interface PicturesSliceState {
  pictures: EntityState<Picture>;
  pictureFolders: EntityState<PicturesFolder>;
}

interface PicturesFolder {
  folderId: string;
  pictureIds: string[];
}

interface ByPictureIdProps {
  pictureId: string;
}
