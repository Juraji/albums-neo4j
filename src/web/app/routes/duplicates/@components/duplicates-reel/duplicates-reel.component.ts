import {ChangeDetectionStrategy, Component, HostListener, OnDestroy, OnInit, Output} from '@angular/core';
import {selectAllDuplicates} from '@ngrx/duplicates';
import {Store} from '@ngrx/store';
import {combineLatest, iif, of, ReplaySubject} from 'rxjs';
import {untilDestroyed} from '@utils/until-destroyed';
import {filterEmpty, once} from '@utils/rx';
import {filter, map, mergeMap} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-duplicates-reel',
  templateUrl: './duplicates-reel.component.html',
  styleUrls: ['./duplicates-reel.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DuplicatesReelComponent implements OnInit, OnDestroy {

  readonly duplicates$ = this.store.select(selectAllDuplicates);

  readonly duplicatesSlice$ = this.duplicates$.pipe(map(it => it.slice(0, 10)));
  readonly hasMore$ = this.duplicates$.pipe(map(it => it.length > 10));

  @Output()
  readonly selectedDuplicate$ = new ReplaySubject<DuplicatesView>(1);

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    combineLatest([this.duplicates$, this.activatedRoute.queryParamMap])
      .pipe(
        untilDestroyed(this),
        filter(([ds]) => !ds.isEmpty()),
        mergeMap(([ds, qp]) => iif(
          () => qp.has('selectTrackingId'),
          of(ds.find(d => d.trackingId$ === qp.get('selectTrackingId'))),
          of(ds[0])
        )),
      )
      .subscribe(d => this.selectedDuplicate$.next(d));
  }

  ngOnDestroy() {
  }

  @HostListener('document:keydown', ['$event'])
  onDocumentKeypress(e: KeyboardEvent) {
    const go = (delta: -1 | 1) =>
      combineLatest([this.duplicates$, this.selectedDuplicate$])
        .pipe(
          once(),
          map(([duplicates, selected]) => {
            const idx = duplicates.findIndex(x => x === selected) + delta;
            if (idx < 0) {
              return duplicates.first();
            } else if (idx >= duplicates.length) {
              return duplicates.last();
            } else {
              return duplicates[idx];
            }
          }),
          filterEmpty(),
        )
        .subscribe(next => this.onSelectDuplicate(next));

    switch (e.key) {
      case 'ArrowLeft':
        go(-1);
        break;
      case 'ArrowRight':
        go(1);
        break;
    }
  }

  onSelectDuplicate(duplicate: DuplicatesView) {
    this.selectedDuplicate$.next(duplicate);
  }
}
