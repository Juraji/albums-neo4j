import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
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
}
