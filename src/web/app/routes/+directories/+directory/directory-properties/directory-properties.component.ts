import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {BooleanToggle} from '@utils/boolean-toggle';
import {Router} from '@angular/router';
import {DirectoriesService} from '@services/directories.service';

@Component({
  selector: 'app-directory-properties',
  templateUrl: './directory-properties.component.html',
  styleUrls: ['./directory-properties.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DirectoryPropertiesComponent {

  @Input()
  directory: Directory | null = null;

  readonly closed$ = new BooleanToggle();

  constructor(
    private readonly router: Router,
    private readonly directoryService: DirectoriesService
  ) {
  }

  onDirectoryAction(directory: Directory) {
    this.router.navigate(['directories', directory.id]);
  }

  onUpdateDirectoryPictures() {
    if (!!this.directory?.id) {
      this.directoryService
        .updateDirectoryPictures(this.directory.id)
        .subscribe();
    }
  }
}
