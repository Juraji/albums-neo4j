import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {BooleanToggle} from '@utils/boolean-toggle';
import {Store} from '@ngrx/store';
import {selectDuplicateCount} from '@ngrx/duplicates';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {selectFolderCount} from '@ngrx/folders';
import {selectTagCount} from '@ngrx/tags';

interface Link {
  label: Observable<string>;
  url: string;
}

@Component({
  selector: 'app-main-navbar',
  templateUrl: './main-navbar.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MainNavbarComponent implements OnInit {

  readonly opened$ = new BooleanToggle();

  readonly folderCount$ = this.store.select(selectFolderCount);
  readonly duplicateCount$ = this.store.select(selectDuplicateCount);
  readonly tagCount$ = this.store.select(selectTagCount);

  readonly links: Link[] = [
    {
      label: this.folderCount$
        .pipe(map(count => `Folders (${count})`)),
      url: '/folders'
    },
    {
      label: this.duplicateCount$
        .pipe(map(count => `Duplicates (${count})`)),
      url: '/duplicates'
    },
    {
      label: this.tagCount$
        .pipe(map(count => `Tags (${count})`)),
      url: '/tags'
    },
    {
      label: of('Settings'),
      url: '/settings'
    },
    {
      label: of('Slideshow'),
      url: '/slideshow'
    }
  ];

  constructor(
    private readonly store: Store<AppState>
  ) {
  }

  ngOnInit(): void {
  }
}
