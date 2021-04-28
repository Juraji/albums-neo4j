import {Component, Input, OnInit} from '@angular/core';
import {from, Observable, ReplaySubject} from 'rxjs';
import {ObserveProperty} from '@utils/decorators';
import {map, mergeMap, share, switchMap, withLatestFrom} from 'rxjs/operators';
import {ROOT_FOLDER_ID} from '../../root-folder';
import {AddFolderModal} from '../add-folder-modal/add-folder.modal';
import {createFolder, deleteFolder, moveFolder} from '@ngrx/folders';
import {Store} from '@ngrx/store';
import {Modals} from '@juraji/ng-bootstrap-modals';
import {Router} from '@angular/router';
import {AddPicturesModal} from '../add-pictures-modal/add-pictures.modal';
import {addPictureSuccess} from '@ngrx/pictures';
import {once, switchMapContinue} from '@utils/rx';
import {FolderSelectorModal} from '@components/folder-selector';
import {runDuplicateScan} from '@ngrx/duplicates';

@Component({
  selector: 'app-folder-details-pane',
  templateUrl: './folder-controls.component.html',
  styleUrls: ['./folder-controls.component.scss']
})
export class FolderControlsComponent implements OnInit {

  @Input()
  public folder: BindingType<Folder>;

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
        switchMapContinue(() => this.modals.open<Folder>(AddFolderModal).onResolved)
      )
      .subscribe(([parentId, folder]) => this.store.dispatch(createFolder(folder, parentId)));
  }

  onMoveFolder() {
    this.folder$
      .pipe(
        once(),
        switchMap(source => this.modals.open<FolderSelectorResult>(FolderSelectorModal, {data: {source}}).onResolved)
      )
      .subscribe(({source, target}) =>
        this.store.dispatch(moveFolder(source.id, target.id)));
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
    const uploadResult = this.folder$
      .pipe(
        once(),
        switchMapContinue(data => this.modals.open<Picture[]>(AddPicturesModal, {data}).onResolved),
        share()
      );

    uploadResult
      .pipe(
        mergeMap(([folder, pictures]) =>
          from(pictures).pipe(map(picture => ({folder, picture})))),
      )
      .subscribe(({folder, picture}) =>
        this.store.dispatch(addPictureSuccess(picture, folder.id)));

    uploadResult
      .pipe(
        switchMap(() => this.modals
          .confirm('Upload complete, do you want to scan for duplicates').onResolved)
      )
      .subscribe(() => {
        this.store.dispatch(runDuplicateScan());
        this.router.navigate(['/duplicates']);
      });
  }
}
