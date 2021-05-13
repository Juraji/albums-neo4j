import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {HTTP_X_DISPLAY_PROGRESS_HEADER} from '@services/http/http-progress.interceptor';
import {bufferCount, catchError, concatMap, finalize, map, mapTo, mergeMap, tap} from 'rxjs/operators';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {retryBackoff} from 'backoff-rxjs';

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

  uploadPictures(folder: Folder, files: File[]): Observable<Picture[]> {
    if (files.length === 0) {
      return of<Picture[]>([]);
    }

    const postUri = `${this.baseUri}/${folder.id}/pictures`;
    const total = files.length;

    const progress$ = new BehaviorSubject<number>(0);
    const progressTitle$ = progress$
      .pipe(map(c => `Uploaded ${c} of ${total} pictures to ${folder.name}...`));

    const shadeRef = this.modals.shade(
      progressTitle$,
      progress$.pipe(map(current => (current / total) * 100))
    );

    const uploadFormData = (file: File) => {
      const fd = new FormData();
      fd.append('files[]', file, file.name);

      return this.httpClient
        .post<Picture[]>(postUri, fd)
        .pipe(
          retryBackoff({
            initialInterval: environment.uploads.retryDelay,
            maxRetries: environment.uploads.maxRetries,
            shouldRetry: e => environment.uploads.retryWhenStatus.includes(e.status)
          })
        );
    };

    return of(files).pipe(
      concatMap(f => f),
      mergeMap(uploadFormData, environment.uploads.maxConcurrent),
      catchError(e => this.modals
        .confirm(`An image failed to upload: ${e.message}.`, 'Ok')
        .onComplete.pipe(mapTo([]))),
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
