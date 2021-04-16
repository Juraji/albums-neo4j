import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PictureThumbnailDirective} from './picture-thumbnail.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [PictureThumbnailDirective],
  exports: [PictureThumbnailDirective]
})
export class PictureImageViewsModule {
}
