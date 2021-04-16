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

  getFolderPictures(folderId: string): Observable<HttpEvent<Picture[]>> {
    return this.httpClient.get<Picture[]>(`${this.baseUri}/${folderId}/pictures`, {
      observe: 'events',
      reportProgress: true
    });
  }

  uploadPictures(folderId: string, files: File[]): Observable<HttpEvent<Picture[]>> {
    const formData = new FormData();
    files.forEach(f => formData.append('files[]', f, f.name));

    return this.httpClient.post<Picture[]>(`${this.baseUri}/${folderId}/pictures`, formData, {
      observe: 'events',
      reportProgress: true
    });
  }
}
