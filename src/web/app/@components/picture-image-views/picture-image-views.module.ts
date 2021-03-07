import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PictureImgDirective} from './picture-img.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [PictureImgDirective],
  exports: [PictureImgDirective]
})
export class PictureImageViewsModule {
}
