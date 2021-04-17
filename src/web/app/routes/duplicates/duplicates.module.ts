import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DuplicatesRoutingModule } from './duplicates-routing.module';
import { ManageDuplicatesPage } from './manage-duplicates/manage-duplicates.page';
import {MainNavbarModule} from '@components/main-navbar';
import {PictureImageViewsModule} from '@components/picture-image-views';
import {FsPipesModule} from '@components/fs-pipes';
import { DuplicatesReelComponent } from './@components/duplicates-reel/duplicates-reel.component';
import { SideBySidePictureComponent } from './@components/side-by-side-picture/side-by-side-picture.component';


@NgModule({
  declarations: [
    ManageDuplicatesPage,
    DuplicatesReelComponent,
    SideBySidePictureComponent
  ],
    imports: [
        CommonModule,
        DuplicatesRoutingModule,
        MainNavbarModule,
        PictureImageViewsModule,
        FsPipesModule
    ]
})
export class DuplicatesModule { }
