import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PictureThumbnailDirective} from './picture-thumbnail.directive';
import {PictureImgDirective} from './picture-img.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [PictureThumbnailDirective, PictureImgDirective],
  exports: [PictureThumbnailDirective, PictureImgDirective]
})
export class PictureImageViewsModule {
}
