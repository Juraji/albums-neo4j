import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TagsRoutingModule } from './tags-routing.module';
import { TagManagementPage } from './tag-management/tag-management.page';
import {MainNavbarModule} from "@components/main-navbar";
import {TagMgmtModule} from "@components/tag-mgmt";


@NgModule({
  declarations: [
    TagManagementPage
  ],
  imports: [
    CommonModule,
    TagsRoutingModule,
    MainNavbarModule,
    TagMgmtModule
  ]
})
export class TagsModule { }
