import {Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {loadRootDirectories} from '@actions/directories.actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent {
  constructor(private readonly store: Store<AppState>) {
    store.dispatch(loadRootDirectories());
  }
}
