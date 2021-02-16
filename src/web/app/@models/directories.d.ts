interface Directory {
  id: string;
  location: string;
  name: string;
  children: Directory[];
}

interface NewDirectoryDto {
  location: string;
}

interface DirectoryCreatedEvent extends AlbumEvent {
  eventType: "DirectoryCreatedEvent";
  directoryId: string;
}

interface DirectoryTreeUpdatedEvent extends AlbumEvent {
  eventType: "DirectoryTreeUpdatedEvent";
  directoryId: string;
}
