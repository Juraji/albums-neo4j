import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '@environment';
import {Observable} from 'rxjs';

export const ROOT_FOLDER_ID = 'ROOT';
export const ROOT_FOLDER: FolderTreeView = {
  id: ROOT_FOLDER_ID,
  name: 'Root',
  children: [],
  isRoot: true
};

@Injectable({
  providedIn: 'root'
})
export class FoldersService {
  private readonly baseUri = `${environment.apiBaseUri}/folders`;

  constructor(private readonly httpClient: HttpClient) {
  }

  getRoots(): Observable<FolderTreeView[]> {
    return this.httpClient.get<FolderTreeView[]>(`${this.baseUri}/roots`);
  }

  createFolder(folder: Folder, parentId?: string): Observable<Folder> {
    const params = !!parentId ? new HttpParams().append('parentId', parentId) : new HttpParams();
    return this.httpClient.post<Folder>(this.baseUri, folder, {params});
  }

  createFolderP(path: string, parentId?: string): Observable<Folder> {
    const params = !!parentId ? new HttpParams().append('parentId', parentId) : new HttpParams();
    const payload: CreateFolderPDto = {path};

    return this.httpClient.post<Folder>(`${this.baseUri}/batch`, payload, {params});
  }

  updateFolder(folder: Folder): Observable<Folder> {
    return this.httpClient.put<Folder>(`${this.baseUri}/${folder.id}`, folder);
  }

  deleteFolder(folderId: string, recursive: boolean): Observable<void> {
    const params = new HttpParams().append('recursive', recursive.toString());
    return this.httpClient.delete<void>(`${this.baseUri}/${folderId}`, {params});
  }

  moveFolder(folderId: string, targetId: string): Observable<Folder> {
    return this.httpClient.post<Folder>(`${this.baseUri}/${folderId}/move-to/${targetId}`, null);
  }
}
