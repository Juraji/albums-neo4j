import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '@environment';

@Injectable({
  providedIn: 'root'
})
export class DuplicatesService {

  constructor(private readonly httpClient: HttpClient) {
  }

  getAllDuplicates(): Observable<DuplicateProps[]> {
    return this.httpClient.get<DuplicateProps[]>(`${environment.apiBaseUri}/duplicates`);
  }

  scanDuplicates(pictureId: string): Observable<DuplicateProps[]> {
    return this.httpClient.post<DuplicateProps[]>(`${environment.apiBaseUri}/pictures/${pictureId}/duplicates/scan`, null);
  }

  unlinkDuplicate(duplicateId: string): Observable<void> {
    return this.httpClient.delete<void>(`${environment.apiBaseUri}/duplicates/${duplicateId}`);
  }
}
