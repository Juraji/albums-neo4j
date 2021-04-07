import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {map, switchMap} from 'rxjs/operators';
import {selectFolderById, selectFolderChildrenById, selectRootFolders} from '@reducers/folders';
import {of} from 'rxjs';
import {filterEmpty} from '@utils/rx';

export const ROOT_FOLDER: FolderTreeView = {
  id: 'root',
  name: 'Root',
  children: [],
  isRoot: true
};

@Component({
  templateUrl: './folder.page.html',
  styleUrls: ['./folder.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FolderPage implements OnInit {

  public readonly folderId$ = this.activatedRoute.paramMap
    .pipe(map(m => m.get('folderId')), filterEmpty());

  public readonly folder$ = this.folderId$.pipe(
    switchMap(folderId => folderId === ROOT_FOLDER.id
      ? of(ROOT_FOLDER)
      : this.store.select(selectFolderById, {folderId}))
  );

  public readonly childFolders$ = this.folderId$
    .pipe(
      switchMap(folderId => folderId === ROOT_FOLDER.id
        ? this.store.select(selectRootFolders)
        : this.store.select(selectFolderChildrenById, {folderId})),
    );

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
  }

  onAddFolder() {
  }
}
