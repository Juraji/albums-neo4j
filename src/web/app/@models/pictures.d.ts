interface Picture {
  id: string
  location: string
  name: string
  width: number
  height: number
  fileSize: number
  fileType: FileType
  lastModified: string
  directory: Directory
  tags: Tag[]
}

interface NewPictureDto {
  location: string
  name?: string
}

type FileType = "JPEG" | "BMP" | "GIF" | "PNG" | "TIFF" | "UNKNOWN"

interface PictureCreatedEvent extends AlbumEvent {
  eventType: "PictureCreatedEvent";
  pictureId: string;
  location: string;
  directoryId: string;

}

interface PictureUpdatedEvent extends AlbumEvent {
  eventType: "PictureUpdatedEvent";
  pictureId: string;
}

interface PictureDeletedEvent extends AlbumEvent {
  eventType: "PictureDeletedEvent";
  pictureId: string;
  location: string;
  doDeleteFile: boolean;
}
