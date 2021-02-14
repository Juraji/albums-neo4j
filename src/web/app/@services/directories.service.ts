import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "@environment";

@Injectable({
  providedIn: 'root'
})
export class DirectoriesService {

  constructor(private readonly httpClient: HttpClient) {
  }

  getRoots(): Observable<Directory[]> {
    return this.httpClient.get<Directory[]>(`${environment.apiBaseUri}/directories/roots`)
  }

  createDirectory(dto: NewDirectoryDto, recursive: any): Observable<Directory> {
    return this.httpClient
      .post<Directory>(`${environment.apiBaseUri}/directories`, dto, {
        params: new HttpParams()
          .append("recursive", recursive)
      })
  }
}
