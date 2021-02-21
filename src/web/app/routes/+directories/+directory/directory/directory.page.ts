import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {filter, map, shareReplay, switchMap} from 'rxjs/operators';
import {selectDirectory} from '@reducers/directories';
import {BehaviorSubject, combineLatest, fromEvent, Observable} from 'rxjs';
import {selectDirectoryLoadState, selectDirectoryPicturesRange} from '@reducers/pictures';
import {environment} from '@environment';
import {untilDestroyed} from '@utils/until-destroyed';
import {fetchDirectoryPictures} from '@actions/pictures.actions';
import {not, sideEffect} from '@utils/rx';

@Component({
  templateUrl: './directory.page.html',
  styleUrls: ['./directory.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
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
    map(([directoryId, {page, size}]) => ({directoryId, page, size})),
    sideEffect(
      (props) => this.store.dispatch(fetchDirectoryPictures(props)),
      ({directoryId}) => this.store.select(selectDirectoryLoadState(directoryId)).pipe(not())),
    switchMap(({directoryId, page, size}) => this.store.select(selectDirectoryPicturesRange(directoryId, page, size))),
    map((pictures) => pictures),
    shareReplay(1)
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
