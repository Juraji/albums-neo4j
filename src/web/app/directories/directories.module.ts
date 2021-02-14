import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {DirectoriesRoutingModule} from './directories-routing.module';
import {DirectoriesOverviewPage} from './directories-overview/directories-overview.page';
import {DirectoryTreeModule} from "@components/directory-tree/directory-tree.module";
import {MainNavbarModule} from "@components/main-navbar/main-navbar.module";
import {DirectoryPage} from './directory/directory.page';
import {AddDirectoryModalModule} from "@components/add-directory-modal/add-directory-modal.module";


@NgModule({
  declarations: [DirectoriesOverviewPage, DirectoryPage],
  imports: [
    CommonModule,
    DirectoriesRoutingModule,
    DirectoryTreeModule,
    MainNavbarModule,
    AddDirectoryModalModule
  ]
})
export class DirectoriesModule {
}
