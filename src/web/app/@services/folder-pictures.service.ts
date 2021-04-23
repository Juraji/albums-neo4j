import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {HTTP_X_DISPLAY_PROGRESS_HEADER} from '@services/http/http-progress.interceptor';

@Injectable({
  providedIn: 'root'
})
export class FolderPicturesService {
  private readonly baseUri = `${environment.apiBaseUri}/folders`;

  constructor(private readonly httpClient: HttpClient) {
  }

  getFolderPictures(folderId: string): Observable<Picture[]> {
    const headers = new HttpHeaders()
      .append(HTTP_X_DISPLAY_PROGRESS_HEADER, 'Downloading pictures...');

    return this.httpClient.get<Picture[]>(
      `${this.baseUri}/${folderId}/pictures`,
      {headers}
    );
  }

  uploadPictures(folderId: string, files: File[]): Observable<Picture[]> {
    const formData = new FormData();
    files.forEach(f => formData.append('files[]', f, f.name));
    const headers = new HttpHeaders()
      .append(HTTP_X_DISPLAY_PROGRESS_HEADER, 'Uploading pictures...');

    return this.httpClient.post<Picture[]>(`${this.baseUri}/${folderId}/pictures`, formData, {headers});
  }
}
