type FileType = 'JPEG' | 'BMP' | 'GIF' | 'PNG' | 'TIFF' | 'UNKNOWN';

interface Picture {
  id: string;
  name: string;
  type: FileType;
  width: number;
  height: number;
  fileSize: number;
  addedOn: string;
  lastModified: string;
}

interface DuplicatesView {
  sourceId: string;
  targetId: string;
  similarity: number;
  trackingId$: string;
}

interface PictureContainerDto {
  picture: Picture;
  folder: Folder;
}
