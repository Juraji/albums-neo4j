import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmptyPipe } from './empty.pipe';
import { NotEmptyPipe } from './not-empty.pipe';



@NgModule({
  imports: [CommonModule],
  declarations: [EmptyPipe, NotEmptyPipe],
  exports: [EmptyPipe, NotEmptyPipe]
})
export class UtilityPipesModule { }
