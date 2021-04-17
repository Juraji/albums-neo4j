import {Component, Inject} from '@angular/core';
import {Store} from '@ngrx/store';
import {MODAL_DATA, ModalRef} from '@juraji/ng-bootstrap-modals';
import {selectAllFolders} from '@ngrx/folders';
import {map} from 'rxjs/operators';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';

@Component({
  templateUrl: './folder-selector.modal.html',
  styleUrls: ['./folder-selector.modal.scss']
})
export class FolderSelectorModal {

  public readonly availableFolders$ = this.store.select(selectAllFolders)
    .pipe(map(folders => folders.filter(f => f.id !== this.modalData.source.id)));

  public readonly form = typedFormGroup<FolderSelectorResult>({
    source: typedFormControl(this.modalData.source),
    target: typedFormControl(this.modalData.source, Validators.required),
  });

  constructor(
    private readonly store: Store<AppState>,
    private readonly modalRef: ModalRef,
    @Inject(MODAL_DATA) public readonly modalData: FolderSelectorData
  ) {
  }

  onSubmit() {
    this.modalRef.resolve(this.form.value);
  }
}
