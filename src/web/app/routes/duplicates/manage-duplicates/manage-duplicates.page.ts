import {Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {runDuplicateScan} from '@ngrx/duplicates';
import {Modals} from '@juraji/ng-bootstrap-modals';

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

  onRunDuplicateScan() {
    this.modals
      .confirm(`Are you sure you want to run the duplicate scan now? This could take some time...`).onResolved
      .subscribe(() => this.store.dispatch(runDuplicateScan()));
  }
}
