import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {DirectoriesService} from '@services/directories.service';
import {Store} from '@ngrx/store';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {EMPTY} from 'rxjs';

@Component({
  selector: 'app-directory-properties',
  templateUrl: './directory-properties.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DirectoryPropertiesComponent {

  @Input()
  directory: Directory | null = null;

  constructor(
    private readonly router: Router,
    private readonly store: Store<AppState>,
    private readonly directoryService: DirectoriesService,
    private readonly modals: Modals,
  ) {
  }

  onDirectoryAction(directory: Directory) {
    this.router.navigate(['directories', directory.id]);
  }

  onUpdateDirectoryPictures() {
    if (!!this.directory?.id) {
      const shadeRef = this.modals.shade(`Updating "${this.directory.name}..."`, EMPTY);

      this.directoryService
        .updateDirectoryPictures(this.directory.id)
        .subscribe({complete: () => shadeRef.dismiss()});
    }
  }
}
