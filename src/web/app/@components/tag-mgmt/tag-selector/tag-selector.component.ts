import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {createTag, createTagSuccess, selectAllTags} from '@ngrx/tags';
import {sideEffect} from '@utils/rx';
import {filter, map, switchMap} from 'rxjs/operators';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {EditTagModal} from '@components/tag-mgmt';
import {Actions, ofType} from '@ngrx/effects';
import {unwrap} from '@utils/rx/unwrap';

@Component({
  selector: 'app-tag-selector',
  templateUrl: './tag-selector.component.html',
  styleUrls: ['./tag-selector.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TagSelectorComponent implements OnInit {

  readonly tags$: Observable<Tag[]> = this.store.select(selectAllTags);

  readonly tagsIsEmpty$ = this.tags$.pipe(map(arr => arr.isEmpty()));

  @Output()
  readonly tagSelected = new EventEmitter<Tag>();

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
    private readonly actions$: Actions,
  ) {
  }

  ngOnInit(): void {
  }

  onTagSelected(tag: Tag) {
    this.tagSelected.emit(tag);
  }

  onCreateTag() {
    this.modals.open<Tag>(EditTagModal)
      .onResolved
      .pipe(
        sideEffect(tag => this.store.dispatch(createTag(tag))),
        switchMap(tag => this.actions$.pipe(
          ofType(createTagSuccess),
          unwrap('tag'),
          filter(t => t.label === tag.label)))
      )
      .subscribe(tag => this.onTagSelected(tag));
  }
}
