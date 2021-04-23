import {Injectable} from '@angular/core';
import {HttpEvent, HttpEventType, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {finalize, tap} from 'rxjs/operators';

export const HTTP_X_DISPLAY_PROGRESS_HEADER = 'x-display-progress';

@Injectable()
export class HttpProgressInterceptor implements HttpInterceptor {

  constructor(
    private readonly modals: Modals,
  ) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (request.headers.has(HTTP_X_DISPLAY_PROGRESS_HEADER)) {
      const message = request.headers.get(HTTP_X_DISPLAY_PROGRESS_HEADER) || 'Loading...';
      const relayRequest = request.clone({
        headers: request.headers.delete(HTTP_X_DISPLAY_PROGRESS_HEADER),
        reportProgress: true
      });

      const progress = new Subject<number>();
      const shadeRef = this.modals.shade(message, progress);

      const onNext = (e: HttpEvent<any>) => {
        if ((e.type === HttpEventType.DownloadProgress || e.type === HttpEventType.UploadProgress) && !!e.total) {
          if (e.loaded === e.total) {
            progress.next(-1);
          } else {
            progress.next(100 * (e.loaded / e.total));
          }
        }
      };

      const onComplete = () => {
        progress.complete();
        shadeRef.dismiss();
      };

      return next.handle(relayRequest)
        .pipe(tap(onNext), finalize(onComplete));
    } else {
      return next.handle(request);
    }
  }
}
