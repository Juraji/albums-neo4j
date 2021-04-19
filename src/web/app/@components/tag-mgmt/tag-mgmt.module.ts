import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReactiveFormsModule} from '@angular/forms';
import {ModalsModule} from '@juraji/ng-bootstrap-modals';
import {ColorTwitterModule} from 'ngx-color/twitter';
import {TagDirective} from './tag.directive';
import {EditTagModal} from './edit-tag/edit-tag.modal';
import {TagSelectorComponent} from './tag-selector/tag-selector.component';
import {NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';


@NgModule({
  declarations: [
    EditTagModal,
    TagDirective,
    TagSelectorComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ModalsModule,
    ColorTwitterModule,
    NgbDropdownModule
  ],
  exports: [
    ModalsModule,
    EditTagModal,
    TagDirective,
    TagSelectorComponent
  ]
})
export class TagMgmtModule {
}
