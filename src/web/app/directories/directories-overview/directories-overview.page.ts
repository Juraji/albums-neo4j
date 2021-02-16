import {Component} from '@angular/core';
import {Store} from "@ngrx/store";
import {Observable} from "rxjs";
import {Router} from "@angular/router";
import {selectDirectoryTree} from "@reducers/directories";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AddDirectoryModal} from "@components/add-directory-modal/add-directory/add-directory.modal";
import {switchMap} from "rxjs/operators";
import {DirectoriesService} from "@services/directories.service";
import {loadRootDirectories} from "@actions/directories.actions";

@Component({
  templateUrl: './directories-overview.page.html'
})
export class DirectoriesOverviewPage {

  readonly directories$: Observable<Directory[]> =
    this.store.select(selectDirectoryTree)

  constructor(
    private readonly store: Store<AppState>,
    private readonly router: Router,
    private readonly directoriesService: DirectoriesService,
    private readonly modalService: NgbModal
  ) {
  }

  reloadRoots() {
    this.store.dispatch(loadRootDirectories())
  }

  addDirectory() {
    const modalRef = this.modalService.open(AddDirectoryModal)
    modalRef.closed
      .pipe(
        switchMap(({location, recursive}: AddDirectoryModalResult) =>
          this.directoriesService.createDirectory({location}, recursive))
      )
      .subscribe()
  }

  onDirectoryAction(directory: Directory) {
    this.router.navigate(["directories", directory.id])
  }
}
