import {Component, Input} from '@angular/core';
import {combineLatest, ReplaySubject} from 'rxjs';
import {Store} from '@ngrx/store';
import {map, shareReplay, switchMap, switchMapTo, take, tap} from 'rxjs/operators';
import {selectPictureById} from '@reducers/pictures';
import {filterEmpty} from '@utils/rx';
import {deletePicture, fetchPicture} from '@actions/pictures.actions';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {unlinkDuplicate} from '@actions/duplicates.actions';
import {ObserveProperty} from '@utils/decorators';

@Component({
  selector: 'app-picture-duplicate',
  templateUrl: './picture-duplicate.component.html',
  styleUrls: ['./picture-duplicate.component.scss']
})
export class PictureDuplicateComponent {

  @Input()
  duplicate?: DuplicateProps;

  @ObserveProperty('duplicate')
  readonly duplicate$ = new ReplaySubject<DuplicateProps>(1);

  readonly sourcePicture$ = this.duplicate$.pipe(
    map(d => d.sourceId),
    switchMap(id => this.store.select(selectPictureById(id))),
    filterEmpty(),
    shareReplay(1)
  );

  readonly targetPicture$ = this.duplicate$.pipe(
    map(d => d.targetId),
    switchMap(id => this.store.select(selectPictureById(id))
      .pipe(tap(p => !p ? this.store.dispatch(fetchPicture(id)) : undefined))),
    filterEmpty(),
    shareReplay(1)
  );

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
  ) {
  }

  onUnlinkDuplicate() {
    combineLatest([this.sourcePicture$, this.targetPicture$])
      .pipe(
        take(1),
        switchMap(([source, target]) => this.modals
          .confirm(`Are you sure you want to unlink ${target.name} from ${source.name}?`).onResolved),
        switchMapTo(this.duplicate$),
      )
      .subscribe(d => this.store.dispatch(unlinkDuplicate(d.id)));
  }

  onDeleteDuplicate() {
    this.targetPicture$
      .pipe(
        take(1),
        switchMap(target => this.modals
          .confirm(`Are you sure you want to delete ${target.name}?`).onResolved),
        switchMapTo(this.targetPicture$),
      )
      .subscribe((target) => this.store.dispatch(deletePicture(target.id)));
  }
}
