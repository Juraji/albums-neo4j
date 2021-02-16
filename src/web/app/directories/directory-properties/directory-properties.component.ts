import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {BooleanToggle} from "@utils/boolean-toggle";
import {Router} from "@angular/router";

@Component({
  selector: 'app-directory-properties',
  templateUrl: './directory-properties.component.html',
  styleUrls: ['./directory-properties.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DirectoryPropertiesComponent {

  @Input()
  directory: Directory | null = null

  readonly closed$ = new BooleanToggle()

  constructor(private readonly router: Router) {
  }

  onDirectoryAction(directory: Directory) {
    this.router.navigate(["directories", directory.id])
  }
}
