import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TagsService {
  private readonly baseUri = `${environment.apiBaseUri}/tags`;

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllTags(): Observable<Tag[]> {
    return this.httpClient.get<Tag[]>(this.baseUri);
  }

  createTag(tag: Tag): Observable<Tag> {
    return this.httpClient.post<Tag>(this.baseUri, tag);
  }

  updateTag(tag: Tag): Observable<Tag> {
    return this.httpClient.put<Tag>(`${this.baseUri}/${tag.id}`, tag);
  }

  deleteTag(tagId: string): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUri}/${tagId}`);
  }
}
