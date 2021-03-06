import {ChangeDetectionStrategy, Component, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {combineLatest, Observable, ReplaySubject} from 'rxjs';
import {map, shareReplay, withLatestFrom} from 'rxjs/operators';
import {filterWhen, once} from '@utils/rx';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaginationComponent implements OnChanges {
  readonly pages$ = new ReplaySubject<number[]>();
  readonly currentPage$ = new ReplaySubject<number>(1);

  readonly canPrevious$ = this.currentPage$
    .pipe(map(p => p > 1), shareReplay(1));
  readonly canNext$ = this.currentPage$
    .pipe(withLatestFrom(this.pages$), map(([p, ps]) => p < ps.length), shareReplay(1));

  readonly displayPages$: Observable<number[]> = combineLatest([this.pages$, this.currentPage$])
    .pipe(map(([pages, currPage]) => pages.slice(
      Math.max(0, Math.min(currPage - 6, pages.length - 11)),
      Math.min(Math.max(11, currPage + 5), pages.length)))
    );

  @Input()
  pageSize: BindingType<number>;

  @Input()
  collectionSize: BindingType<number>;

  @Output()
  readonly currentPage: Observable<number> = this.currentPage$;

  @Output()
  readonly range: Observable<PageRange> = this.currentPage$
    .pipe(
      withLatestFrom(p => [p, this.pageSize || 0]),
      map(([page, limit]) => {
        const start = (page - 1) * limit;
        const end = start + limit;
        return {start, end};
      })
    );

  constructor() {
  }

  ngOnChanges(changes: SimpleChanges) {
    const pageSize = changes.hasOwnProperty('pageSize')
      ? changes.pageSize.currentValue
      : this.pageSize || 0;
    const collectionSize = changes.hasOwnProperty('collectionSize')
      ? changes.collectionSize.currentValue
      : this.collectionSize || 0;

    const pageCount = Math.ceil(collectionSize / pageSize);
    const pageArr = Array.from(Array(pageCount).keys(), (_, i) => ++i);

    this.pages$.next(pageArr);
    this.currentPage$.next(1);
  }

  onPageSelect(p: number) {
    this.currentPage$.next(p);
  }

  onPrevious() {
    this.currentPage$
      .pipe(once(), filterWhen(() => this.canPrevious$))
      .subscribe(p => this.onPageSelect(p - 1));
  }

  onNext() {
    this.currentPage$
      .pipe(once(), filterWhen(() => this.canNext$))
      .subscribe(p => this.onPageSelect(p + 1));
  }
}
