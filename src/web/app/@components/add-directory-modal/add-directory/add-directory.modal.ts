import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ModalRef} from "@juraji/ng-bootstrap-modals";

@Component({
  templateUrl: './add-directory.modal.html',
  styleUrls: ['./add-directory.modal.scss']
})
export class AddDirectoryModal {

  readonly form = new FormGroup({
    location: new FormControl("", [Validators.required]),
    recursive: new FormControl(true)
  })

  constructor(private readonly modalRef: ModalRef) {
  }

  dismiss() {
    this.modalRef.dismiss()
  }

  addDirectory() {
    this.modalRef.resolve(this.form.value)
  }
}
