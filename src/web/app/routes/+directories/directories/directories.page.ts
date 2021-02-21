import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';
import {selectDirectoryTree} from '@reducers/directories';
import {AddDirectoryModal} from '@components/add-directory-modal/add-directory/add-directory.modal';
import {switchMap} from 'rxjs/operators';
import {DirectoriesService} from '@services/directories.service';
import {loadRootDirectories} from '@actions/directories.actions';
import {Modals} from '@juraji/ng-bootstrap-modals';

@Component({
  templateUrl: './directories.page.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DirectoriesPage {

  readonly directories$: Observable<Directory[]> =
    this.store.select(selectDirectoryTree);

  constructor(
    private readonly store: Store<AppState>,
    private readonly router: Router,
    private readonly directoriesService: DirectoriesService,
    private readonly modalService: Modals
  ) {
  }

  reloadRoots() {
    this.store.dispatch(loadRootDirectories());
  }

  addDirectory() {
    this.modalService
      .open<any, AddDirectoryModalResult>(AddDirectoryModal)
      .onResolved
      .pipe(switchMap(({location, recursive}) =>
        this.directoriesService.createDirectory({location}, recursive)))
      .subscribe();
  }

  onDirectoryAction(directory: Directory) {
    this.router.navigate(['directories', directory.id]);
  }
}
