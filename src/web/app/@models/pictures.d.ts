type FileType = 'JPEG' | 'BMP' | 'GIF' | 'PNG' | 'TIFF' | 'UNKNOWN';

interface Picture {
  id: string;
  name: string;
  type: FileType;
  width: number;
  height: number;
  fileSize: number;
  addedOn: string;
}

interface DuplicatesView {
  source: Picture;
  target: Picture;
  similarity: number;
}
