import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TagSelectorComponent} from './tag-selector/tag-selector.component';
import {NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';
import {ReactiveFormsModule} from '@angular/forms';


@NgModule({
  declarations: [TagSelectorComponent],
  imports: [
    CommonModule,
    NgbDropdownModule,
    ReactiveFormsModule
  ],
  exports: [TagSelectorComponent]
})
export class TagSelectorModule {
}
