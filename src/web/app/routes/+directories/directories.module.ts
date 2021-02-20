import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {DirectoriesRoutingModule} from './directories-routing.module';
import {DirectoryTreeModule} from '@components/directory-tree/directory-tree.module';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import {DirectoriesPage} from './directories/directories.page';
import {AddDirectoryModalModule} from '@components/add-directory-modal/add-directory-modal.module';


@NgModule({
  declarations: [DirectoriesPage],
  imports: [
    CommonModule,
    DirectoriesRoutingModule,
    DirectoryTreeModule,
    AddDirectoryModalModule,
    MainNavbarModule,
  ]
})
export class DirectoriesModule {
}
