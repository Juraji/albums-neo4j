import {ChangeDetectionStrategy, Component, HostListener, OnDestroy, OnInit, Output} from '@angular/core';
import {selectAllDuplicates} from '@ngrx/duplicates';
import {Store} from '@ngrx/store';
import {combineLatest, ReplaySubject} from 'rxjs';
import {untilDestroyed} from '@utils/until-destroyed';
import {filterEmpty, filterEmptyArray, once} from '@utils/rx';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-duplicates-reel',
  templateUrl: './duplicates-reel.component.html',
  styleUrls: ['./duplicates-reel.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DuplicatesReelComponent implements OnInit, OnDestroy {

  readonly duplicates$ = this.store.select(selectAllDuplicates);

  @Output()
  readonly selectedDuplicate$ = new ReplaySubject<DuplicatesView>(1);

  constructor(
    private readonly store: Store<AppState>,
  ) {
  }

  ngOnInit(): void {
    this.duplicates$
      .pipe(
        untilDestroyed(this),
        filterEmptyArray(),
        map(ds => ds[0])
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
