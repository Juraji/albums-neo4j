import {Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {loadAllDuplicates, unlinkDuplicate} from '@ngrx/duplicates';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {deletePicture, movePicture} from '@ngrx/pictures';
import {FolderSelectorModal} from '@components/folder-selector';
import {ROOT_FOLDER} from '../../folders/root-folder';

@Component({
  templateUrl: './manage-duplicates.page.html',
  styleUrls: ['./manage-duplicates.page.scss']
})
export class ManageDuplicatesPage {

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
  ) {
  }

  onUnlinkDuplicate(duplicate: DuplicatesView) {
    const msg = `Are you sure you want to unlink ${duplicate.target.name} from ${duplicate.source.name}?`;

    this.modals.confirm(msg).onResolved
      .subscribe(() => this.store.dispatch(unlinkDuplicate(duplicate.source.id, duplicate.target.id)));
  }

  onDeletePicture(picture: Picture) {
    const msg = `Are you sure you want to delete ${picture.name}?`;

    this.modals.confirm(msg).onResolved
      .subscribe(() => this.store.dispatch(deletePicture(picture.id)));
  }

  onMovePicture(picture: Picture) {
    this.modals.open<FolderSelectorResult>(FolderSelectorModal, {data: {source: ROOT_FOLDER}}).onResolved
      .subscribe(({target}) => this.store.dispatch(movePicture(picture.id, target.id)));
  }

  onReloadDuplicates() {
    this.store.dispatch(loadAllDuplicates());
  }
}
