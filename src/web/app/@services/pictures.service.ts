import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {environment} from '@environment';

@Injectable({
  providedIn: 'root'
})
export class PicturesService {


  constructor(private readonly httpClient: HttpClient) {
  }

  getPicture(pictureId: string): Observable<PictureProps> {
    return this.httpClient.get<PictureProps>(`${environment.apiBaseUri}/pictures/${pictureId}`);
  }

  getPicturesByDirectory(directoryId: string, page: number, size: number): Observable<PictureProps[]> {
    return this.httpClient.get<PictureProps[]>(`${environment.apiBaseUri}/directories/${directoryId}/pictures`, {
      params: new HttpParams()
        .append('page', `${page}`)
        .append('size', `${size}`)
    });
  }
}