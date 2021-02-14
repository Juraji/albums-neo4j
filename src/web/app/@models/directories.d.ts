interface Directory {
  id: string
  location: string
  name: string
  children: Directory[]
}

interface NewDirectoryDto {
  location: string
}

interface DirectoryProps {
  id: string
  location: string
  name: string
}
