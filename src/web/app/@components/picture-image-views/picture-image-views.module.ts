import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PictureCardDirective} from './picture-card.directive';
import {PictureFullViewDirective} from './picture-full-view.directive';


@NgModule({
  imports: [CommonModule],
  declarations: [
    PictureCardDirective,
    PictureFullViewDirective
  ],
  exports: [
    PictureCardDirective,
    PictureFullViewDirective
  ]
})
export class PictureImageViewsModule {
}
