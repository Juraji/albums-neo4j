import {ChangeDetectionStrategy, Component, Inject} from '@angular/core';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';
import {MODAL_DATA, ModalRef} from '@juraji/ng-bootstrap-modals';
import {Store} from '@ngrx/store';
import {selectAllFolders} from '@ngrx/folders';
import {map} from 'rxjs/operators';

export interface TargetFolderForm {
  folderId: string;
  targetFolderId: string;
}

@Component({
  templateUrl: './move-folder.modal.html',
  styleUrls: ['./move-folder.modal.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MoveFolderModal {

  public readonly availableFolders$ = this.store.select(selectAllFolders)
    .pipe(map(folders => folders.filter(f => f.id !== this.folder.id)));

  public readonly form = typedFormGroup<TargetFolderForm>({
    folderId: typedFormControl(this.folder.id, Validators.required),
    targetFolderId: typedFormControl('', Validators.required),
  });

  constructor(
    private readonly store: Store<AppState>,
    private readonly modalRef: ModalRef,
    @Inject(MODAL_DATA) public readonly folder: Folder,
  ) {
  }

  onSubmit() {
    this.modalRef.resolve(this.form.value);
  }
}
