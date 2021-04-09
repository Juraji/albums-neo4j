import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {FoldersRoutingModule} from './folders-routing.module';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import { FolderPage } from './folder/folder.page';
import {UtilityPipesModule} from '@components/utility-pipes/utility-pipes.module';
import { AddFolderModal } from './add-folder-modal/add-folder.modal';
import {NgbmodModalsModule} from '@juraji/ng-bootstrap-modals';
import {ReactiveFormsModule} from '@angular/forms';


@NgModule({
  declarations: [FolderPage, AddFolderModal],
  imports: [
    CommonModule,
    FoldersRoutingModule,
    MainNavbarModule,
    UtilityPipesModule,
    NgbmodModalsModule,
    ReactiveFormsModule,
  ]
})
export class FoldersModule {
}
