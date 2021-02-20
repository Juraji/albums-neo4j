import {Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {filter, map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {selectDirectory} from '@reducers/directories';
import {BehaviorSubject, Observable} from 'rxjs';
import {selectPicturesRange} from '@reducers/pictures';
import {IInfiniteScrollEvent} from 'ngx-infinite-scroll';
import {insurePictureRange} from '@actions/pictures.actions';
import {environment} from '@environment';

@Component({
  templateUrl: './directory.page.html',
})
export class DirectoryPage {

  private readonly offSetLimitSelection$ = new BehaviorSubject<{ page: number; size: number }>({
    page: 0,
    size: environment.defaultPicturePageSize
  });

  private readonly directoryId$: Observable<string> = this.activatedRoute.paramMap.pipe(
    filter((m) => m.has('directoryId')),
    map((m) => m.get('directoryId') as string)
  );

  readonly directoryProps$: Observable<Directory> = this.directoryId$.pipe(
    switchMap((directoryId) => this.store.select(selectDirectory, {directoryId}))
  );

  readonly directoryPictures$: Observable<PictureProps[]> = this.directoryId$.pipe(
    withLatestFrom(this.offSetLimitSelection$),
    map(([directoryId, {page, size}]) => ({directoryId, page, size})),
    tap((props) => this.store.dispatch(insurePictureRange(props))),
    switchMap((props) => this.store.select(selectPicturesRange, props))
  );

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute
  ) {
  }

  onInfiniteScrollUp($event: IInfiniteScrollEvent) {
    console.log($event);
  }

  onInfiniteScrollDown($event: IInfiniteScrollEvent) {
    console.log($event);
  }
}
