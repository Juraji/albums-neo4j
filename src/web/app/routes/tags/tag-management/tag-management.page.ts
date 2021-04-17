import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {createTag, deleteTag, selectAllTags, updateTag} from '@ngrx/tags';
import {Modals} from "@juraji/ng-bootstrap-modals";
import {EditTagModal} from "@components/tag-mgmt";

@Component({
  templateUrl: './tag-management.page.html',
  styleUrls: ['./tag-management.page.scss']
})
export class TagManagementPage {

  public readonly availableTags$ = this.store.select(selectAllTags);

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
  ) {
  }

  onAddNewTag() {
    this.modals.open<Tag>(EditTagModal).onResolved
      .subscribe(tag => this.store.dispatch(createTag(tag)));
  }

  onEditTag(tag: Tag) {
    this.modals.open<Tag>(EditTagModal, {data: tag}).onResolved
      .subscribe(update => this.store.dispatch(updateTag(update)));
  }

  onDeleteTag(tag: Tag) {
    this.modals.confirm(`Are you sure you want to delete tag "${tag.label}"?`).onResolved
      .subscribe(() => this.store.dispatch(deleteTag(tag)));
  }
}
