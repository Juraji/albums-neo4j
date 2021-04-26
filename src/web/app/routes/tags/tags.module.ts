import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TagsRoutingModule } from './tags-routing.module';
import { TagManagementPage } from './tag-management/tag-management.page';
import {MainNavbarModule} from '@components/main-navbar';
import {TagMgmtModule} from '@components/tag-mgmt';
import {PaginationModule} from '@components/pagination/pagination.module';
import {PictureImageViewsModule} from '@components/picture-image-views';


@NgModule({
  declarations: [
    TagManagementPage,
  ],
  imports: [
    CommonModule,
    TagsRoutingModule,
    MainNavbarModule,
    TagMgmtModule,
    PaginationModule,
    PictureImageViewsModule
  ]
})
export class TagsModule { }
