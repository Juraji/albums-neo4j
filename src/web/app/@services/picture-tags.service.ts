import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PictureTagsService {
  private readonly baseUri = `${environment.apiBaseUri}/pictures`;

  constructor(private readonly httpClient: HttpClient) {
  }

  getPictureTags(pictureId: string): Observable<Tag[]> {
    return this.httpClient.get<Tag[]>(`${this.baseUri}/${pictureId}/tags`);
  }

  addTagToPicture(pictureId: string, tag: Tag): Observable<Tag> {
    return this.httpClient.post<Tag>(`${this.baseUri}/${pictureId}/tags`, tag);
  }

  removeTagFromPicture(pictureId: string, tagId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUri}/${pictureId}/tags/${tagId}`);
  }
}
