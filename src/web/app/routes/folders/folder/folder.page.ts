import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {ActivatedRoute} from '@angular/router';
import {map, switchMap} from 'rxjs/operators';
import {selectFolderById} from '@ngrx/folders';
import {of} from 'rxjs';
import {filterEmpty} from '@utils/rx';
import {ROOT_FOLDER, ROOT_FOLDER_ID} from '../root-folder';

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

  constructor(
    private readonly store: Store<AppState>,
    private readonly activatedRoute: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
  }
}
