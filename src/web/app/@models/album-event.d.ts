interface AlbumEvent {
  eventType: string;
}

interface PictureAddedEvent extends AlbumEvent {
  eventType: 'PictureAddedEvent';
  folderId: string;
  picture: Picture;
}

interface PictureHashGeneratedEvent extends AlbumEvent {
  eventType: 'PictureHashGeneratedEvent';
  pictureId: string;
}

interface DuplicatePictureDetectedEvent extends AlbumEvent {
  eventType: 'DuplicatePictureDetectedEvent';
  sourceId: string;
  targetId: string;
  similarity: string;
}
