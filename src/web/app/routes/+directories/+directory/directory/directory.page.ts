import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {selectDirectory} from '@reducers/directories';
import {combineLatest, Observable, Subject} from 'rxjs';
import {filter, map, startWith, switchMap} from 'rxjs/operators';
import {selectDirectoryPictures, selectDirectoryPicturesRange} from '@reducers/pictures';
import {fetchDirectoryPictures} from '@actions/pictures.actions';
import {untilDestroyed} from '@utils/until-destroyed';
import {distinctUntilChangedArrayExact} from '@utils/rx';

@Component({
  templateUrl: './directory.page.html',
  styleUrls: ['./directory.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DirectoryPage implements OnInit, OnDestroy {
  private readonly directoryId$: Observable<string> = this.activatedRoute.paramMap.pipe(
    filter((m) => m.has('directoryId')),
    map((m) => m.get('directoryId') as string)
  );

  readonly directoryProps$: Observable<Directory> = this.directoryId$.pipe(
    switchMap((directoryId) => this.store.select(selectDirectory(directoryId)))
  );

  readonly directoryPictureCount$: Observable<number> = this.directoryId$.pipe(
    switchMap(id => this.store.select(selectDirectoryPictures(id))),
    map(l => l.length)
  );

  readonly page$ = new Subject<number>();
  readonly pageSize$ = new Subject<number>();
  readonly pictures$ = combineLatest([
    this.directoryId$,
    this.page$.pipe(startWith(1)),
    this.pageSize$.pipe(startWith(50))
  ]).pipe(
    distinctUntilChangedArrayExact(),
    switchMap(([id, page, size]) => this.store.select(selectDirectoryPicturesRange(id, page, size))),
    map(props => props.chunks(4).transpose2d())
  );

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    this.directoryId$
      .pipe(untilDestroyed(this))
      .subscribe(id => this.store.dispatch(fetchDirectoryPictures(id)));
  }

  ngOnDestroy(): void {
  }

}
