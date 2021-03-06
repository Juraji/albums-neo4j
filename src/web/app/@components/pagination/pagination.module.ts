import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from './pagination/pagination.component';
import { PageSizeSelectorComponent } from './page-size-selector/page-size-selector.component';



@NgModule({
  declarations: [PaginationComponent, PageSizeSelectorComponent],
  imports: [
    CommonModule
  ],
  exports: [PaginationComponent, PageSizeSelectorComponent]
})
export class PaginationModule { }
