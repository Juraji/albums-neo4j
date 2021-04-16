import {Component, Input, OnInit} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {ObserveProperty} from '@utils/decorators';
import {map} from 'rxjs/operators';
import {ROOT_FOLDER_ID} from '../root-folder';
import {AddFolderModal} from '../add-folder-modal/add-folder.modal';
import {createFolder, deleteFolder, moveFolder} from '@ngrx/folders';
import {Store} from '@ngrx/store';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {Router} from '@angular/router';
import {MoveFolderModal, TargetFolderForm} from '../move-folder-modal/move-folder.modal';

@Component({
  selector: 'app-folder-details-pane',
  templateUrl: './folder-controls.component.html',
  styleUrls: ['./folder-controls.component.scss']
})
export class FolderControlsComponent implements OnInit {

  @Input()
  public folder: Folder | undefined | null;

  @ObserveProperty('folder')
  public readonly folder$: Observable<Folder> = new ReplaySubject();

  public readonly isRootFolder$ = this.folder$.pipe(map(f => f.id === ROOT_FOLDER_ID));

  constructor(
    private readonly store: Store<AppState>,
    private readonly modals: Modals,
    private readonly router: Router
  ) {
  }

  ngOnInit(): void {
  }

  onAddFolder() {
    this.modals.open<Folder>(AddFolderModal).onResolved
      .subscribe(folder => this.store.dispatch(createFolder(folder, this.getRealFolderId())));
  }

  onMoveFolder() {
    const fid = this.getRealFolderId();
    if (!!fid) {
      this.modals.open<TargetFolderForm>(MoveFolderModal, {data: this.folder}).onResolved
        .subscribe(({targetFolderId}) => this.store.dispatch(moveFolder(fid, targetFolderId)));
    }
  }

  onDeleteFolder() {
    const fid = this.getRealFolderId();
    if (!!fid) {
      this.modals.confirm('Are you sure you want to delete this folder?').onResolved
        .subscribe(() => {
          this.store.dispatch(deleteFolder(fid, true));
          this.router.navigate(['/folders']);
        });
    }
  }

  private getRealFolderId(): string | undefined {
    return this.folder?.id === ROOT_FOLDER_ID
      ? undefined
      : this.folder?.id;
  }
}
