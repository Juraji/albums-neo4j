import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {selectAllTags, selectTagsLoaded} from '@reducers/tags';
import {conditionalSideEffect, not, sideEffect} from '@utils/rx';
import {createTag, createTagSuccess, loadAllTags} from '@actions/tags.actions';
import {filter, map, shareReplay, switchMap} from 'rxjs/operators';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {EditTagModal} from '../edit-tag/edit-tag.modal';
import {Actions, ofType} from '@ngrx/effects';

@Component({
  selector: 'app-tag-selector',
  templateUrl: './tag-selector.component.html',
  styleUrls: ['./tag-selector.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TagSelectorComponent implements OnInit {

  readonly tags$: Observable<Tag[]> = this.store.select(selectAllTags)
    .pipe(
      conditionalSideEffect(
        () => this.store.select(selectTagsLoaded).pipe(not()),
        () => this.store.dispatch(loadAllTags())
      ),
      shareReplay(1)
    );

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
    this.modals.open<any, Tag>(EditTagModal)
      .onResolved
      .pipe(
        sideEffect(tag => this.store.dispatch(createTag(tag))),
        switchMap(tag => this.actions$.pipe(ofType(createTagSuccess), filter(t => t.label === tag.label)))
      )
      .subscribe(tag => this.onTagSelected(tag));
  }
}
