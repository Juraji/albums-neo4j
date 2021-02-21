import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TagsRoutingModule } from './tags-routing.module';
import { TagsManagementPage } from './tags-management/tags-management.page';
import {MainNavbarModule} from '@components/main-navbar';
import {TagMgmtModule} from '@components/tag-mgmt';


@NgModule({
  declarations: [TagsManagementPage],
  imports: [
    CommonModule,
    TagsRoutingModule,
    MainNavbarModule,
    TagMgmtModule
  ]
})
export class TagsModule { }
