import {Component, OnInit} from '@angular/core';
import {AppState} from "@reducers/index";
import {Store} from "@ngrx/store";
import {Observable} from "rxjs";
import {loadRootDirectories} from "@actions/directories.actions";
import {Router} from "@angular/router";

@Component({
  templateUrl: './directories-overview.page.html'
})
export class DirectoriesOverviewPage implements OnInit {

  directories$: Observable<Directory[]> = this.store.select((s) => s.directories.tree)

  constructor(
    private readonly store: Store<AppState>,
    private readonly router: Router
  ) {
  }

  ngOnInit(): void {
    this.reloadRoots()
  }

  reloadRoots() {
    this.store.dispatch(loadRootDirectories())
  }

  onDirectoryAction(directory: Directory) {
    this.router.navigate(["directories", directory.id])
  }
}
