import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Store} from '@ngrx/store';
import {map, switchMap} from 'rxjs/operators';
import {selectTreePathByFolderId} from '@ngrx/folders';
import {ActivatedRoute} from '@angular/router';
import {filterEmpty} from '@utils/rx';

@Component({
  selector: 'app-folder-breadcrumbs',
  templateUrl: './folder-breadcrumbs.component.html',
  styleUrls: ['./folder-breadcrumbs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FolderBreadcrumbsComponent {

  readonly crumbs$ = this.activatedRoute.paramMap
    .pipe(
      map(m => m.get('folderId')),
      filterEmpty(),
      switchMap(folderId => this.store.select(selectTreePathByFolderId, {folderId}))
    );

  constructor(private readonly store: Store<AppState>,
              private readonly activatedRoute: ActivatedRoute
  ) {
  }

}
