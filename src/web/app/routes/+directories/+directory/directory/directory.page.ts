import {Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {selectDirectory} from '@reducers/directories';
import {BehaviorSubject, combineLatest, fromEvent, Observable} from 'rxjs';
import {isPictureSetFullyLoaded, selectPicturesRange} from '@reducers/pictures';
import {insurePictureRange} from '@actions/pictures.actions';
import {environment} from '@environment';
import {untilDestroyed} from '@utils/until-destroyed';
import {filterAsync} from '@utils/filter-async.rx-pipe';

@Component({
  templateUrl: './directory.page.html',
  styleUrls: ['./directory.page.scss']
})
export class DirectoryPage implements OnInit, OnDestroy {
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

  readonly directoryPictures$: Observable<PictureProps[]> = combineLatest([this.directoryId$, this.offSetLimitSelection$]).pipe(
    filterAsync(([directoryId]) => this.store.select(isPictureSetFullyLoaded, {directoryId}).pipe(map(s => !s))),
    map(([directoryId, {page, size}]) => ({directoryId, page, size})),
    tap((props) => this.store.dispatch(insurePictureRange(props))),
    switchMap((props) => this.store.select(selectPicturesRange, props))
  );

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    fromEvent<KeyboardEvent>(document, 'keydown')
      .pipe(untilDestroyed(this), filter(e => e.key === 'End'))
      .subscribe(this.loadMore);
  }

  ngOnDestroy(): void {
  }

  loadMore() {
    const current = this.offSetLimitSelection$.value;
    this.offSetLimitSelection$.next(current.copy({page: current.page + 1}));
  }
}
