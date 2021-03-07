import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {loadRootDirectories} from '@actions/directories.actions';
import {fetchAllDuplicates} from '@actions/duplicates.actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
  constructor(private readonly store: Store<AppState>) {
    store.dispatch(loadRootDirectories());
    store.dispatch(fetchAllDuplicates());
  }
}
