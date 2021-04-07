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

  getThumbnailUri(pictureId: string): string {
    return `${this.baseUri}/${pictureId}/thumbnail`;
  }

  getDownloadUri(pictureId: string): string {
    return `${this.baseUri}/${pictureId}/download`;
  }

  deleteDuplicateFromPicture(pictureId: string, targetId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUri}/${pictureId}/duplicates/${targetId}`);
  }
}
