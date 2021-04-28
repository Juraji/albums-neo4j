import {ChangeDetectionStrategy, Component, Input, OnInit, Output} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {environment} from '@environment';

@Component({
  selector: 'app-page-size-selector',
  templateUrl: './page-size-selector.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageSizeSelectorComponent implements OnInit {

  readonly selectedSize$ = new BehaviorSubject(environment.defaultPageSize);

  @Input()
  sizeOptions: BindingType<number[]> = environment.pageSizeOptions;

  @Input()
  collectionSize: BindingType<number>;

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
