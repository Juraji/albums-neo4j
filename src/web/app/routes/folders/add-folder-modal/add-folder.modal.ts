import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ModalRef} from '@juraji/ng-bootstrap-modals';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';

@Component({
  templateUrl: './add-folder.modal.html',
  styleUrls: ['./add-folder.modal.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddFolderModal {

  public readonly form = typedFormGroup<Omit<Folder, 'id'>>({
    name: typedFormControl('', Validators.required)
  });

  constructor(private readonly modalRef: ModalRef) {
  }

  onSubmit() {
    this.modalRef.resolve(this.form.value);
  }
}
