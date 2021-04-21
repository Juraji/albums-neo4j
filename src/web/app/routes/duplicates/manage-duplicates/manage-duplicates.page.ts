import {Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {loadAllDuplicates} from '@ngrx/duplicates';

@Component({
  templateUrl: './manage-duplicates.page.html',
  styleUrls: ['./manage-duplicates.page.scss']
})
export class ManageDuplicatesPage {

  constructor(
    private readonly store: Store<AppState>,
  ) {
  }

  onReloadDuplicates() {
    this.store.dispatch(loadAllDuplicates());
  }
}
