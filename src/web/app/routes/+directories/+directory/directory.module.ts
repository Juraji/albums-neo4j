import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {DirectoryRoutingModule} from './directory-routing.module';
import {DirectoryPage} from './directory/directory.page';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import {DirectoryPropertiesComponent} from './directory-properties/directory-properties.component';
import {DirectoryTreeModule} from '@components/directory-tree/directory-tree.module';
import {PictureImageViewsModule} from '@components/picture-image-views/picture-image-views.module';
import {PaginationModule} from '@components/pagination/pagination.module';


@NgModule({
  declarations: [DirectoryPage, DirectoryPropertiesComponent],
  imports: [
    CommonModule,
    DirectoryRoutingModule,
    MainNavbarModule,
    DirectoryTreeModule,
    PictureImageViewsModule,
    PaginationModule,
  ]
})
export class DirectoryModule {
}
