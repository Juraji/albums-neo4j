import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DuplicatesRoutingModule } from './duplicates-routing.module';
import { DuplicatesPage } from './duplicates/duplicates.page';
import {MainNavbarModule} from '@components/main-navbar';
import { DuplicateViewComponent } from './duplicate-view/duplicate-view.component';
import {PictureImageViewsModule} from '@components/picture-image-views';
import {FsPipesModule} from '@components/fs-pipes';


@NgModule({
  declarations: [DuplicatesPage, DuplicateViewComponent],
  imports: [
    CommonModule,
    DuplicatesRoutingModule,
    MainNavbarModule,
    PictureImageViewsModule,
    FsPipesModule
  ]
})
export class DuplicatesModule { }
