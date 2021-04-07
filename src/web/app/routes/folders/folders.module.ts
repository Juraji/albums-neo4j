import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {FoldersRoutingModule} from './folders-routing.module';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import { FolderPage } from './folder/folder.page';
import {UtilityPipesModule} from '@components/utility-pipes/utility-pipes.module';


@NgModule({
  declarations: [FolderPage],
  imports: [
    CommonModule,
    FoldersRoutingModule,
    MainNavbarModule,
    UtilityPipesModule,
  ]
})
export class FoldersModule {
}
