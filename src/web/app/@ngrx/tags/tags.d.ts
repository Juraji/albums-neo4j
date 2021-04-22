interface TagsSliceState {
  tags: EntityState<Tag>;
  pictureTags: EntityState<PictureTags>;
}

interface PictureTags {
  pictureId: string;
  tagIds: string[];
}
