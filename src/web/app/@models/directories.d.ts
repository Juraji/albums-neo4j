interface Directory {
  id: string;
  location: string;
  name: string;
  children: Directory[];
}

interface DirectoryProps {
  id: string;
  location: string;
  name: string;
}

interface NewDirectoryDto {
  location: string;
}

interface DirectoryTreeUpdatedEvent extends AlbumEvent {
  eventType: 'DirectoryTreeUpdatedEvent';
  directoryId: string;
}
