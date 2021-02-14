import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeRoutingModule } from './home-routing.module';
import { HomePage } from './home.page';
import {DirectoryTreeModule} from "@components/directory-tree/directory-tree.module";
import {MainNavbarModule} from "@components/main-navbar/main-navbar.module";


@NgModule({
  declarations: [HomePage],
  imports: [
    CommonModule,
    HomeRoutingModule,
    DirectoryTreeModule,
    MainNavbarModule
  ]
})
export class HomeModule { }
