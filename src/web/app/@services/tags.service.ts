import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TagsService {

  constructor(
    private readonly httpClient: HttpClient
  ) {
  }

  getAllTags(): Observable<Tag[]> {
    return this.httpClient.get<Tag[]>(`${environment.apiBaseUri}/tags`);
  }

  createTag(newTag: NewTagDto): Observable<Tag> {
    return this.httpClient.post<Tag>(`${environment.apiBaseUri}/tags`, newTag);
  }

  updateTag(tag: Tag): Observable<Tag> {
    return this.httpClient.put<Tag>(`${environment.apiBaseUri}/tags`, tag);
  }

  deleteTag(tag: Tag): Observable<void> {
    return this.httpClient.delete<void>(`${environment.apiBaseUri}/tags/${tag.id}`);
  }
}
