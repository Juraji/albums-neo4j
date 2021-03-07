import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {of, ReplaySubject} from 'rxjs';
import {Store} from '@ngrx/store';
import {concatMap, shareReplay, switchMap, withLatestFrom} from 'rxjs/operators';
import {selectDuplicatesByPicture} from '@reducers/duplicates';
import {scanDuplicatesForPicture, unlinkDuplicate} from '@actions/duplicates.actions';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {ObserveProperty} from '@utils/decorators';

@Component({
  selector: 'app-picture-duplicates',
  templateUrl: './picture-duplicates.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PictureDuplicatesComponent {

  @Input()
  picture: PictureProps | null = null;

  @ObserveProperty('picture')
  readonly picture$ = new ReplaySubject<PictureProps>(1);

  readonly duplicates$ = this.picture$.pipe(
    switchMap(({id}) => this.store.select(selectDuplicatesByPicture(id))),
    shareReplay(1)
  );

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
  ) {
  }

  onScanDuplicates() {
    if (!!this.picture) {
      this.store.dispatch(scanDuplicatesForPicture(this.picture.id));
    }
  }

  onUnlinkAll() {
    this.modals
      .confirm(`Are you sure you want to unlink all duplicates from ${this.picture?.name}?`)
      .onResolved
      .pipe(
        withLatestFrom(this.duplicates$),
        concatMap(([_, duplicates]) => of(...duplicates))
      )
      .subscribe(duplicate => this.store.dispatch(unlinkDuplicate(duplicate.id)));
  }
}
