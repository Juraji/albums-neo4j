import {Component, OnInit} from '@angular/core';
import {Store} from "@ngrx/store";
import {Observable} from "rxjs";
import {loadRootDirectories} from "@actions/directories.actions";
import {Router} from "@angular/router";
import {selectDirectoryTree} from "@reducers/directories";

@Component({
  templateUrl: './directories-overview.page.html'
})
export class DirectoriesOverviewPage implements OnInit {

  readonly directories$: Observable<Directory[]> =
    this.store.select(selectDirectoryTree)

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
