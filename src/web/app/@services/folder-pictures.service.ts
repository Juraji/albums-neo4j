import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient, HttpEvent} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FolderPicturesService {
  private readonly baseUri = `${environment.apiBaseUri}/folders`;

  constructor(private readonly httpClient: HttpClient) {
  }

  getFolderPictures(folderId: string): Observable<Picture[]> {
    return this.httpClient.get<Picture[]>(`${this.baseUri}/${folderId}/pictures`);
  }

  uploadPictures(folderId: string, files: FileList): Observable<HttpEvent<Picture[]>> {
    const formData = new FormData();
    Array.from(files).forEach(f => formData.append('files[]', f, f.name));

    return this.httpClient.post<Picture[]>(`${this.baseUri}/${folderId}/pictures`, formData, {
      observe: 'events',
      reportProgress: true
    });
  }
}