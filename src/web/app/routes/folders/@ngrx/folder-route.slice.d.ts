interface FolderRouteSliceState {
  selectedPictures: EntityState<Picture>;
}

type FolderRouteAppState = AppState & FolderRouteSliceState;
