import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '@environment';

@Injectable({
  providedIn: 'root'
})
export class DirectoriesService {

  constructor(private readonly httpClient: HttpClient) {
  }

  getRoots(fromId?: string): Observable<Directory[]> {
    let params = new HttpParams();

    if (!!fromId) {
      params = params.append('fromId', fromId);
    }

    return this.httpClient.get<Directory[]>(`${environment.apiBaseUri}/directories/roots`, {params});
  }

  createDirectory(dto: NewDirectoryDto, recursive: any): Observable<Directory> {
    return this.httpClient
      .post<Directory>(`${environment.apiBaseUri}/directories`, dto, {
        params: new HttpParams()
          .append('recursive', recursive)
      });
  }

  updateDirectoryPictures(directoryId: string): Observable<void> {
    return this.httpClient.post<void>(`${environment.apiBaseUri}/directories/${directoryId}/update`, null);
  }
}
