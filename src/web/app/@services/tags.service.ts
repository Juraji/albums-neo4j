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
}
