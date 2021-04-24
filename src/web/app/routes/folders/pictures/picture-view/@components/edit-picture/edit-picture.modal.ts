import {ChangeDetectionStrategy, Component, Inject} from '@angular/core';
import {MODAL_DATA, ModalRef} from '@juraji/ng-bootstrap-modals';
import {typedFormControl, typedFormGroup} from 'ngx-forms-typed';
import {Validators} from '@angular/forms';

@Component({
  templateUrl: './edit-picture.modal.html',
  styleUrls: ['./edit-picture.modal.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditPictureModal {

  public readonly form = typedFormGroup<Picture>({
    id: typedFormControl(this.picture.id, Validators.required),
    name: typedFormControl(this.picture.name, Validators.required),
    type: typedFormControl(this.picture.type, Validators.required),
    width: typedFormControl(this.picture.width, Validators.required),
    height: typedFormControl(this.picture.height, Validators.required),
    fileSize: typedFormControl(this.picture.fileSize, Validators.required),
    addedOn: typedFormControl(this.picture.addedOn, Validators.required),
    lastModified: typedFormControl(this.picture.lastModified, Validators.required),
  });

  constructor(
    private readonly modalRef: ModalRef,
    @Inject(MODAL_DATA) public readonly picture: Picture,
  ) {
  }

  onSubmit() {
    this.modalRef.resolve(this.form.value);
  }
}
