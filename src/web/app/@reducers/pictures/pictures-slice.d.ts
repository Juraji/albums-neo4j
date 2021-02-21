interface PicturesSliceState {
  pictures: EntityState<PictureProps>;
  directoryLoadStates: Record<string, boolean>;
}
