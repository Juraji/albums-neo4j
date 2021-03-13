import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ObserveProperty} from '@utils/decorators';
import {ReplaySubject} from 'rxjs';
import {shareReplay, switchMap, switchMapTo, take} from 'rxjs/operators';
import {Store} from '@ngrx/store';
import {selectPictureById} from '@reducers/pictures';
import {filterEmpty, not, sideEffect} from '@utils/rx';
import {deletePicture, fetchPicture} from '@actions/pictures.actions';
import {unlinkDuplicate} from '@actions/duplicates.actions';
import {Modals} from '@juraji/ng-bootstrap-modals';

@Component({
  selector: 'app-duplicate-view',
  templateUrl: './duplicate-view.component.html',
  styleUrls: ['./duplicate-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DuplicateViewComponent implements OnInit {

  @Input()
  pictureId: string | null = null;

  @Input()
  duplicateId: string | null = null;

  @ObserveProperty('pictureId')
  readonly pictureId$ = new ReplaySubject<string>(1);

  readonly picture$ = this.pictureId$.pipe(
    sideEffect(
      id => this.store.dispatch(fetchPicture(id)),
      id => this.store.select(selectPictureById(id)).pipe(not())),
    switchMap(id => this.store.select(selectPictureById(id))),
    filterEmpty(),
    shareReplay(1)
  );

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
  ) {
  }

  ngOnInit(): void {
  }

  onUnlink() {
    if (!!this.duplicateId) {
      const duplicateId = this.duplicateId;
      this.modals
        .confirm('Are you sure you want to unlink this duplicate?')
        .onResolved
        .subscribe(() => this.store.dispatch(unlinkDuplicate(duplicateId)));
    }
  }

  onMove() {

  }

  onDelete() {
    this.picture$
      .pipe(
        take(1),
        switchMap(p => this.modals
          .confirm(`Are you sure you want to delete ${p.name}?`).onResolved),
        switchMapTo(this.picture$),
      )
      .subscribe(p => this.store.dispatch(deletePicture(p.id)));
  }
}
