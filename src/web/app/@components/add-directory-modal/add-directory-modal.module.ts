import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AddDirectoryModal} from './add-directory/add-directory.modal';
import {NgbModalModule} from "@ng-bootstrap/ng-bootstrap";
import {ReactiveFormsModule} from "@angular/forms";


@NgModule({
  declarations: [AddDirectoryModal],
  imports: [
    CommonModule,
    NgbModalModule,
    ReactiveFormsModule
  ],
  exports: [
    NgbModalModule,
    AddDirectoryModal
  ],
  entryComponents: [AddDirectoryModal]
})
export class AddDirectoryModalModule {
}
