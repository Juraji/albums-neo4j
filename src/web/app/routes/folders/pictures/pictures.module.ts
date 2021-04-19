import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PicturesRoutingModule} from './pictures-routing.module';
import {PictureViewPage} from './picture-view/picture-view.page';
import {MainNavbarModule} from '@components/main-navbar';
import {PictureDetailsComponent} from './picture-view/@components/picture-details/picture-details.component';
import {FsPipesModule} from '@components/fs-pipes';
import {PictureImageViewsModule} from '@components/picture-image-views';


@NgModule({
  declarations: [
    PictureViewPage,
    PictureDetailsComponent,
  ],
  imports: [
    CommonModule,
    PicturesRoutingModule,
    MainNavbarModule,
    FsPipesModule,
    PictureImageViewsModule
  ]
})
export class PicturesModule {
}
