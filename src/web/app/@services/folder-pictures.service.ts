import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {HTTP_X_DISPLAY_PROGRESS_HEADER} from '@services/http/http-progress.interceptor';
import {bufferCount, catchError, finalize, map, mergeMap, tap} from 'rxjs/operators';
import {Modals} from '@juraji/ng-bootstrap-modals';

@Injectable({
  providedIn: 'root'
})
export class FolderPicturesService {
  private readonly baseUri = `${environment.apiBaseUri}/folders`;

  constructor(
    private readonly httpClient: HttpClient,
    private readonly modals: Modals
  ) {
  }

  getFolderPictures(folderId: string): Observable<Picture[]> {
    const headers = new HttpHeaders()
      .append(HTTP_X_DISPLAY_PROGRESS_HEADER, 'Downloading pictures...');

    return this.httpClient.get<Picture[]>(
      `${this.baseUri}/${folderId}/pictures`,
      {headers}
    );
  }

  uploadPictures(folderId: string, files: File[]): Observable<Picture[]> {
    const postUri = `${this.baseUri}/${folderId}/pictures`;
    const total = files.length;
    const progress$ = new BehaviorSubject<number>(0);
    const shadeRef = this.modals.shade(
      `Uploading ${total} pictures...`,
      progress$.pipe(map(current => (current / total) * 100))
    );

    const uploadFormData = (file: File) => {
      const fd = new FormData();
      fd.append('files[]', file, file.name);

      return this.httpClient
        .post<Picture[]>(postUri, fd)
        .pipe(catchError(e => {
          console.error(e);
          return of([]);
        }));
    };

    return of(...files).pipe(
      mergeMap(uploadFormData, environment.maxConcurrentUpload),
      tap(() => progress$.next(progress$.value + 1)),
      bufferCount(total),
      map(res => res.flatMap(x => x)),
      finalize(() => {
        progress$.complete();
        shadeRef.dismiss();
      })
    );
  }
}
