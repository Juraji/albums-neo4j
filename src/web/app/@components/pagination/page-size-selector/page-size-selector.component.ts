import {ChangeDetectionStrategy, Component, Input, OnInit, Output} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';

const DEFAULT_SIZE_OPTS = [50, 100, 200];

@Component({
  selector: 'app-page-size-selector',
  templateUrl: './page-size-selector.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageSizeSelectorComponent implements OnInit {

  readonly selectedSize$ = new BehaviorSubject(50);

  @Input()
  sizeOptions: number[] | null = DEFAULT_SIZE_OPTS;

  @Input()
  collectionSize: number | null = null;

  @Output()
  readonly selectedSize: Observable<number> = this.selectedSize$;

  constructor() {
  }

  ngOnInit(): void {
  }

  onSizeSelect(event: Event) {
    const element = event.target as HTMLInputElement;
    this.selectedSize$.next(parseInt(element.value, 10));
  }
}
