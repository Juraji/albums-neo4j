import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PicturesRoutingModule} from './pictures-routing.module';
import {PicturePage} from './picture/picture.page';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import { PicturePropertiesComponent } from './picture-properties/picture-properties.component';
import {FsPipesModule} from '@components/fs-pipes/fs-pipes.module';
import { PictureTagsComponent } from './picture-tags/picture-tags.component';
import {TagSelectorModule} from '@components/tag-selector/tag-selector.module';


@NgModule({
  declarations: [PicturePage, PicturePropertiesComponent, PictureTagsComponent],
  imports: [
    CommonModule,
    PicturesRoutingModule,
    MainNavbarModule,
    FsPipesModule,
    TagSelectorModule
  ]
})
export class PicturesModule {
}
