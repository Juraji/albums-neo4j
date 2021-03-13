import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '@environment';

@Injectable({
  providedIn: 'root'
})
export class PicturesService {


  constructor(private readonly httpClient: HttpClient) {
  }

  getPicture(pictureId: string): Observable<PictureProps> {
    return this.httpClient.get<PictureProps>(`${environment.apiBaseUri}/pictures/${pictureId}`);
  }

  getPicturesByDirectory(directoryId: string): Observable<PictureProps[]> {
    return this.httpClient.get<PictureProps[]>(`${environment.apiBaseUri}/directories/${directoryId}/pictures`);
  }

  addTag(pictureId: string, tagId: string): Observable<void> {
    return this.httpClient.post<void>(`${environment.apiBaseUri}/pictures/${pictureId}/tags/${tagId}`, null);
  }

  removeTag(pictureId: string, tagId: string): Observable<void> {
    return this.httpClient.delete<void>(`${environment.apiBaseUri}/pictures/${pictureId}/tags/${tagId}`);
  }

  deletePicture(pictureId: string, deleteFile: boolean): Observable<void> {
    return this.httpClient.delete<void>(`${environment.apiBaseUri}/pictures/${pictureId}`, {
      params: new HttpParams().append('deleteFile', deleteFile.toString())
    });
  }
}
