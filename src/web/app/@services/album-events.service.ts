import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '@environment';
import {filter} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AlbumEventsService {

  readonly events$: Observable<AlbumEvent>;

  constructor() {
    this.events$ = this.createRootSubscription();
  }

  ofType<T extends AlbumEvent, E extends T['eventType'] = any>(eventType: E): Observable<T> {
    return this.events$.pipe(filter(e => e.eventType === eventType)) as Observable<T>;
  }

  private createRootSubscription(): Observable<AlbumEvent> {
    return new Observable((observer) => {
      const endpoint = `${environment.apiBaseUri}/events`;
      const eventSource = new EventSource(endpoint);

      eventSource.onmessage = (e) => {
        const json = JSON.parse(e.data);
        observer.next(json);
      };

      eventSource.onerror = (e) => {
        if (eventSource.readyState === 0) {
          eventSource.close();
          observer.complete();
        } else {
          observer.error(`Event source error: ${e}`);
        }
      };
    });
  }
}
