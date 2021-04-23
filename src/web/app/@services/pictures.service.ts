import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PicturesService {
  private readonly baseUri = `${environment.apiBaseUri}/pictures`;

  constructor(private readonly httpClient: HttpClient) {
  }

  getPicture(pictureId: string): Observable<PictureContainerDto> {
    return this.httpClient.get<PictureContainerDto>(`${this.baseUri}/${pictureId}`);
  }

  getThumbnailUri(pictureId: string): string {
    return `${this.baseUri}/${pictureId}/thumbnail`;
  }

  getDownloadUri(pictureId: string): string {
    return `${this.baseUri}/${pictureId}/download`;
  }

  deleteDuplicateFromPicture(pictureId: string, targetId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUri}/${pictureId}/duplicates/${targetId}`);
  }

  movePicture(pictureId: string, targetFolderId: string): Observable<Picture> {
    return this.httpClient.post<Picture>(`${this.baseUri}/${pictureId}/move-to/${targetFolderId}`, null);
  }

  deletePicture(pictureId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUri}/${pictureId}`);
  }
}
