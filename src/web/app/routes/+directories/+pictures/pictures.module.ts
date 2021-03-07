import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PicturesRoutingModule} from './pictures-routing.module';
import {PicturePage} from './picture/picture.page';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import {PicturePropertiesComponent} from './picture-properties/picture-properties.component';
import {FsPipesModule} from '@components/fs-pipes/fs-pipes.module';
import {PictureTagsComponent} from './picture-tags/picture-tags.component';
import {NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';
import {PictureImageViewsModule} from '@components/picture-image-views/picture-image-views.module';
import {TagMgmtModule} from '@components/tag-mgmt';
import {PictureDuplicatesComponent} from './picture-duplicates/picture-duplicates.component';
import { PictureDuplicateComponent } from './picture-duplicates/picture-duplicate.component';
import {UtilityPipesModule} from '@components/utility-pipes/utility-pipes.module';


@NgModule({
  declarations: [PicturePage, PicturePropertiesComponent, PictureTagsComponent, PictureDuplicatesComponent, PictureDuplicateComponent],
  imports: [
    CommonModule,
    PicturesRoutingModule,
    MainNavbarModule,
    FsPipesModule,
    TagMgmtModule,
    NgbDropdownModule,
    PictureImageViewsModule,
    UtilityPipesModule,
  ]
})
export class PicturesModule {
}
