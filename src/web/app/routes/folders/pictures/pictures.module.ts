import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PicturesRoutingModule} from './pictures-routing.module';
import {PictureViewPage} from './picture-view/picture-view.page';
import {MainNavbarModule} from '@components/main-navbar';
import {PictureDetailsComponent} from './picture-view/@components/picture-details/picture-details.component';
import {FsPipesModule} from '@components/fs-pipes';
import {PictureImageViewsModule} from '@components/picture-image-views';
import { PictureTagsComponent } from './picture-view/@components/picture-tags/picture-tags.component';
import {TagMgmtModule} from '@components/tag-mgmt';
import {NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';
import { PictureDuplicatesComponent } from './picture-view/@components/picture-duplicates/picture-duplicates.component';


@NgModule({
  declarations: [
    PictureViewPage,
    PictureDetailsComponent,
    PictureTagsComponent,
    PictureDuplicatesComponent,
  ],
  imports: [
    CommonModule,
    PicturesRoutingModule,
    MainNavbarModule,
    FsPipesModule,
    PictureImageViewsModule,
    TagMgmtModule,
    NgbDropdownModule
  ]
})
export class PicturesModule {
}
