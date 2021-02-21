import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {selectAllTags, selectTagsLoaded} from '@reducers/tags';
import {not, conditionalSideEffect} from '@utils/rx';
import {createTag, deleteTag, loadAllTags, updateTag} from '@actions/tags.actions';
import {map, shareReplay} from 'rxjs/operators';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {EditTagModal} from '@components/tag-mgmt';

@Component({
  templateUrl: './tags-management.page.html',
  styleUrls: ['./tags-management.page.scss']
})
export class TagsManagementPage implements OnInit {

  readonly tags$ = this.store.select(selectAllTags)
    .pipe(
      conditionalSideEffect(
        () => this.store.select(selectTagsLoaded).pipe(not()),
        () => this.store.dispatch(loadAllTags())
      ),
      shareReplay(1)
    );

  readonly tagsIsEmpty$ = this.tags$.pipe(map(arr => arr.isEmpty()));

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals
  ) {
  }

  ngOnInit(): void {
  }

  onCreateTag() {
    this.modals.open(EditTagModal)
      .onResolved
      .subscribe((tag: Tag) => this.store.dispatch(createTag(tag)));
  }

  onEditTag(tag: Tag) {
    this.modals.open(EditTagModal, {data: tag})
      .onResolved
      .subscribe((editedTag: Tag) => this.store.dispatch(updateTag(editedTag)));
  }

  onDeleteTag(tag: Tag) {
    this.modals.confirm(`Are you sure you want to delete tag "${tag.label}"?`)
      .onResolved
      .subscribe(() => this.store.dispatch(deleteTag(tag)));
  }
}
