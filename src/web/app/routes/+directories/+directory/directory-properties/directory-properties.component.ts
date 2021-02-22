import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {DirectoriesService} from '@services/directories.service';
import {Store} from '@ngrx/store';
import {updateSetting} from '@actions/settings.actions';
import {switchMapTo, take, tap} from 'rxjs/operators';
import {BooleanToggle} from '@utils/boolean-toggle';
import {selectSetting} from '@reducers/settings';
import {untilDestroyed} from '@utils/until-destroyed';

@Component({
  selector: 'app-directory-properties',
  templateUrl: './directory-properties.component.html',
  styleUrls: ['./directory-properties.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DirectoryPropertiesComponent implements OnInit, OnDestroy {

  @Input()
  directory: Directory | null = null;

  readonly closed$ = new BooleanToggle();

  constructor(
    private readonly router: Router,
    private readonly store: Store<AppState>,
    private readonly directoryService: DirectoriesService
  ) {
  }

  ngOnInit(): void {
    this.store
      .select(selectSetting('directory-properties-collapsed', false))
      .pipe(
        take(1),
        tap(s => this.closed$.next(s)),
        switchMapTo(this.closed$),
        untilDestroyed(this)
      )
      .subscribe(state => this.store
        .dispatch(updateSetting('directory-properties-collapsed', state)));
  }

  ngOnDestroy(): void {
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

  onToggleSideBar() {
    this.closed$.toggle();
  }
}
