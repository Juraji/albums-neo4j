import {Component, Input, OnInit} from '@angular/core';
import {ObserveProperty} from '@utils/decorators';
import {Observable, ReplaySubject} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {ROOT_FOLDER_ID} from '../root-folder';
import {selectFolderChildrenById, selectRootFolders} from '@ngrx/folders';
import {filterEmptyArray} from '@utils/rx';
import {Store} from '@ngrx/store';

@Component({
  selector: 'app-folder-children-tiles',
  templateUrl: './folder-children-tiles.component.html',
  styleUrls: ['./folder-children-tiles.component.scss']
})
export class FolderChildrenTilesComponent implements OnInit {

  @Input()
  public folder: Folder | undefined | null;

  @ObserveProperty('folder')
  public readonly folder$: Observable<Folder> = new ReplaySubject();

  public readonly childFolders$ = this.folder$
    .pipe(
      switchMap(({id: folderId}) => folderId === ROOT_FOLDER_ID
        ? this.store.select(selectRootFolders)
        : this.store.select(selectFolderChildrenById, {folderId})),
      filterEmptyArray(),
      map(folders => folders.sort((a, b) => a.name.localeCompare(b.name)))
    );

  constructor(
    private readonly store: Store<AppState>
  ) {
  }

  ngOnInit(): void {
  }

}
