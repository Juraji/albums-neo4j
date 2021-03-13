import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {Store} from '@ngrx/store';
import {selectAllDuplicates} from '@reducers/duplicates';
import {map, shareReplay, take, withLatestFrom} from 'rxjs/operators';

@Component({
  templateUrl: './duplicates.page.html',
  styleUrls: ['./duplicates.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DuplicatesPage implements OnInit {

  readonly duplicates$: Observable<DuplicateProps[]> =
    this.store.select(selectAllDuplicates).pipe(shareReplay(1));

  readonly selectedIndex$ = new ReplaySubject<number>(1);
  readonly selectedDuplicate$: Observable<DuplicateProps> = this.selectedIndex$.pipe(
    withLatestFrom(this.duplicates$),
    map(([idx, ds]) => ds[idx])
  );

  constructor(
    private readonly store: Store<AppState>
  ) {
  }

  ngOnInit(): void {
  }

  onDuplicateSelect(index: number) {
    this.selectedIndex$.next(index);
  }

  onNextDuplicate() {
    this.selectedIndex$
      .pipe(
        take(1),
        withLatestFrom(this.duplicates$),
        map(([idx, ds]) => !!ds[idx + 1] ? idx + 1 : idx)
      )
      .subscribe(next => this.selectedIndex$.next(next));
  }
}
