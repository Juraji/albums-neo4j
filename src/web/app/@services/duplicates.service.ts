import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {HTTP_X_DISPLAY_PROGRESS_HEADER} from '@services/http/http-progress.interceptor';

@Injectable({
  providedIn: 'root'
})
export class DuplicatesService {
  private readonly baseUri = `${environment.apiBaseUri}/duplicates`;

  constructor(private readonly httpClient: HttpClient) {
  }

  getDuplicates(): Observable<DuplicatesView[]> {
    return this.httpClient.get<DuplicatesView[]>(this.baseUri);
  }

  runScan(): Observable<DuplicatesView[]> {
    const headers = new HttpHeaders().append(HTTP_X_DISPLAY_PROGRESS_HEADER, 'Scanning duplicates...');
    return this.httpClient.post<DuplicatesView[]>(`${this.baseUri}/run-scan`, null, {headers});
  }
}
