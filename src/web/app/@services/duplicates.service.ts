import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

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
    return this.httpClient.post<DuplicatesView[]>(`${this.baseUri}/run-scan`, null);
  }
}
