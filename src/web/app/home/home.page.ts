import {Component, OnInit} from '@angular/core';
import {AppState} from "@reducers/index";
import {Store} from "@ngrx/store";
import {Observable} from "rxjs";
import {loadRootDirectories} from "@actions/directories.actions";

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements OnInit {

  directories$: Observable<Directory[]> = this.store.select((s) => s.directories.tree)

  constructor(private readonly store: Store<AppState>) {
  }

  ngOnInit(): void {
    this.reloadRoots()
  }

  reloadRoots() {
    this.store.dispatch(loadRootDirectories())
  }

}
