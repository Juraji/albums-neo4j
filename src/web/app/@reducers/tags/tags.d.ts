interface TagsSliceState {
  tags: TagMap;
  tagsLoaded: boolean;
}

type TagMap = Record<string, Tag>;

interface LoadAllTagsSuccessProps {
  tags: Tag[];
}
