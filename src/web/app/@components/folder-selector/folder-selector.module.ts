import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FolderSelectorModal } from './folder-selector/folder-selector.modal';
import {ReactiveFormsModule} from '@angular/forms';
import {ModalsModule} from '@juraji/ng-bootstrap-modals';



@NgModule({
  declarations: [
    FolderSelectorModal
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ModalsModule
  ],
  exports: [
    FolderSelectorModal
  ]
})
export class FolderSelectorModule { }
