import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {DirectoryRoutingModule} from './directory-routing.module';
import {DirectoryPage} from './directory/directory.page';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import {DirectoryPropertiesComponent} from './directory-properties/directory-properties.component';
import {DirectoryTreeModule} from '@components/directory-tree/directory-tree.module';
import {InfiniteScrollModule} from 'ngx-infinite-scroll';


@NgModule({
  declarations: [DirectoryPage, DirectoryPropertiesComponent],
  imports: [
    CommonModule,
    DirectoryRoutingModule,
    MainNavbarModule,
    DirectoryTreeModule,
    InfiniteScrollModule
  ]
})
export class DirectoryModule {
}
