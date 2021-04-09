import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {map, switchMap} from 'rxjs/operators';
import {createFolder, deleteFolder, selectFolderById, selectFolderChildrenById, selectRootFolders} from '@ngrx/folders';
import {of} from 'rxjs';
import {filterEmpty, filterEmptyArray} from '@utils/rx';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {AddFolderModal} from '../add-folder-modal/add-folder.modal';

export const ROOT_FOLDER_ID = 'root';
export const ROOT_FOLDER: FolderTreeView = {
  id: ROOT_FOLDER_ID,
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
    switchMap(folderId => folderId === ROOT_FOLDER_ID
      ? of(ROOT_FOLDER)
      : this.store.select(selectFolderById, {folderId}))
  );

  public readonly childFolders$ = this.folderId$
    .pipe(
      switchMap(folderId => folderId === ROOT_FOLDER_ID
        ? this.store.select(selectRootFolders)
        : this.store.select(selectFolderChildrenById, {folderId})),
      filterEmptyArray(),
      map(folders => folders.sort((a, b) => a.name.localeCompare(b.name)))
    );

  public readonly isRootFolder$ = this.folderId$.pipe(map(id => id === ROOT_FOLDER_ID));

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute,
    private readonly modals: Modals
  ) {
  }

  ngOnInit(): void {
  }

  onAddFolder(id: string) {
    const realParentId = id === ROOT_FOLDER_ID ? undefined : id;
    this.modals.open<Folder>(AddFolderModal).onResolved
      .subscribe(folder => this.store.dispatch(createFolder(folder, realParentId)));
  }

  onMoveFolder(id: string) {

  }

  onDeleteFolder(id: string) {
    this.modals.confirm('Are you sure you want to delete this folder?').onResolved
      .subscribe(() => this.store.dispatch(deleteFolder(id, true)));
  }
}
