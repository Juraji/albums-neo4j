import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AddDirectoryModal} from './add-directory/add-directory.modal';
import {ReactiveFormsModule} from "@angular/forms";
import {NgbmodModalsModule} from "@juraji/ng-bootstrap-modals";


@NgModule({
  declarations: [AddDirectoryModal],
  imports: [
    CommonModule,
    NgbmodModalsModule,
    ReactiveFormsModule
  ],
  exports: [
    AddDirectoryModal,
    NgbmodModalsModule,
  ]
})
export class AddDirectoryModalModule {
}
