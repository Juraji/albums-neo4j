import {Component} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  templateUrl: './add-directory.modal.html',
  styleUrls: ['./add-directory.modal.scss']
})
export class AddDirectoryModal {

  readonly form = new FormGroup({
    location: new FormControl("", [Validators.required]),
    recursive: new FormControl(true)
  })

  constructor(private readonly activeModal: NgbActiveModal) {
  }

  dismiss() {
    this.activeModal.dismiss()
  }

  addDirectory() {
    this.activeModal.close(this.form.value)
  }
}
