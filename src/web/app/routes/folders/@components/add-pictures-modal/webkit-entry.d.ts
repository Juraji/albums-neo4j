interface DataTransferItem {
  webkitGetAsEntry(): FileSystemEntry | null;
}

interface FileSystemEntry {
  fullPath: string;
  isDirectory: boolean;
  isFile: boolean;
  name: string;
}

interface FileSystemFileEntry extends FileSystemEntry {
  isDirectory: false;
  isFile: true;

  file(successCallback: (file: File) => void, errorCallback: (err: any) => void): void;
}

interface FileSystemDirectoryReader {
  readEntries(successCallback: (entries: FileSystemEntry[]) => void, errorCallback: (err: any) => void): void;
}

interface FileSystemDirectoryEntry extends FileSystemEntry {
  isDirectory: true;
  isFile: false;

  createReader(): FileSystemDirectoryReader;
}
