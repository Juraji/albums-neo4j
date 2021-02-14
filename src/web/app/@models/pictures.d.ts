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
