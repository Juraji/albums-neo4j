interface TagsSliceState {
  tags: TagMap;
  tagsLoaded: boolean;
}

type TagMap = Record<string, Tag>;
