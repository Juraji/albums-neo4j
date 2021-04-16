import {Component, Input, OnInit} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {ObserveProperty} from '@utils/decorators';
import {map, mergeMap, switchMap, withLatestFrom} from 'rxjs/operators';
import {ROOT_FOLDER_ID} from '../root-folder';
import {AddFolderModal} from '../add-folder-modal/add-folder.modal';
import {createFolder, deleteFolder, moveFolder} from '@ngrx/folders';
import {Store} from '@ngrx/store';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {Router} from '@angular/router';
import {MoveFolderModal, TargetFolderForm} from '../move-folder-modal/move-folder.modal';
import {AddPicturesModal} from '../add-pictures-modal/add-pictures.modal';
import {fromArray} from 'rxjs/internal/observable/fromArray';
import {addPictureSuccess} from '@ngrx/pictures';
import {once} from '@utils/rx';

@Component({
  selector: 'app-folder-details-pane',
  templateUrl: './folder-controls.component.html',
  styleUrls: ['./folder-controls.component.scss']
})
export class FolderControlsComponent implements OnInit {

  @Input()
  public folder: Folder | undefined | null;

  @ObserveProperty('folder')
  public readonly folder$: Observable<Folder> = new ReplaySubject(1);

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
    this.folder$
      .pipe(
        once(),
        map(f => f.id === ROOT_FOLDER_ID ? undefined : f.id),
        switchMap(parentId => this.modals.open<Folder>(AddFolderModal).onResolved
          .pipe(withLatestFrom(folder => ({parentId, folder}))))
      )
      .subscribe(({parentId, folder}) => this.store.dispatch(createFolder(folder, parentId)));
  }

  onMoveFolder() {
    this.folder$
      .pipe(
        once(),
        switchMap(data => this.modals.open<TargetFolderForm>(MoveFolderModal, {data}).onResolved)
      )
      .subscribe(({folderId, targetFolderId}) => this.store.dispatch(moveFolder(folderId, targetFolderId)));
  }

  onDeleteFolder() {
    this.folder$
      .pipe(
        once(),
        switchMap(f => this.modals
          .confirm(`Are you sure you want to delete the folder "${f.name}"?`).onResolved
          .pipe(withLatestFrom(() => f.id))
        )
      )
      .subscribe(folderId => {
        this.store.dispatch(deleteFolder(folderId, true));
        this.router.navigate(['/folders']);
      });
  }

  onAddPictures() {
    this.folder$
      .pipe(
        once(),
        switchMap(data => this.modals.open<Picture[]>(AddPicturesModal, {data}).onResolved
          .pipe(
            mergeMap(pictures => fromArray(pictures)),
            withLatestFrom(picture => ({parentId: data.id, picture}))
          )),
      )
      .subscribe(({parentId, picture}) => this.store.dispatch(addPictureSuccess(picture, parentId)));
  }
}
