import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PicturesRoutingModule} from './pictures-routing.module';
import {PictureViewPage} from './picture-view/picture-view.page';
import {MainNavbarModule} from '@components/main-navbar';
import {PictureDetailsComponent} from './@components/picture-details/picture-details.component';
import {FsPipesModule} from '@components/fs-pipes';
import {PictureImageViewsModule} from '@components/picture-image-views';
import {PictureTagsComponent} from './@components/picture-tags/picture-tags.component';
import {TagMgmtModule} from '@components/tag-mgmt';
import {NgbDropdownModule} from '@ng-bootstrap/ng-bootstrap';
import {PictureDuplicatesComponent} from './@components/picture-duplicates/picture-duplicates.component';
import {ReactiveFormsModule} from '@angular/forms';
import {UtilityPipesModule} from '@components/utility-pipes/utility-pipes.module';


@NgModule({
  declarations: [
    PictureViewPage,
    PictureDetailsComponent,
    PictureTagsComponent,
    PictureDuplicatesComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PicturesRoutingModule,
    MainNavbarModule,
    FsPipesModule,
    PictureImageViewsModule,
    TagMgmtModule,
    NgbDropdownModule,
    UtilityPipesModule
  ]
})
export class PicturesModule {
}
