import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FolderSelectorModal } from './folder-selector/folder-selector.modal';
import {ReactiveFormsModule} from '@angular/forms';
import {NgbmodModalsModule} from '@juraji/ng-bootstrap-modals';



@NgModule({
  declarations: [
    FolderSelectorModal
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgbmodModalsModule
  ],
  exports: [
    FolderSelectorModal
  ]
})
export class FolderSelectorModule { }
