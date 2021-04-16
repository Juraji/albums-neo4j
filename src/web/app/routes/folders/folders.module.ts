import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {FoldersRoutingModule} from './folders-routing.module';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import {FolderPage} from './folder/folder.page';
import {UtilityPipesModule} from '@components/utility-pipes/utility-pipes.module';
import {AddFolderModal} from './add-folder-modal/add-folder.modal';
import {NgbmodModalsModule} from '@juraji/ng-bootstrap-modals';
import {ReactiveFormsModule} from '@angular/forms';
import {StoreModule} from '@ngrx/store';
import {reducer} from './@ngrx';
import {FolderBreadcrumbsComponent} from './folder-breadcrumbs/folder-breadcrumbs.component';
import {FolderControlsComponent} from './folder-controls/folder-controls.component';
import {MoveFolderModal} from './move-folder-modal/move-folder.modal';
import {AddPicturesModal} from './add-pictures-modal/add-pictures.modal';
import {PictureImageViewsModule} from '@components/picture-image-views';
import {PaginationModule} from '@components/pagination/pagination.module';


@NgModule({
  declarations: [
    FolderPage,
    AddFolderModal,
    FolderBreadcrumbsComponent,
    FolderControlsComponent,
    MoveFolderModal,
    AddPicturesModal,
  ],
  imports: [
    CommonModule,
    FoldersRoutingModule,
    MainNavbarModule,
    UtilityPipesModule,
    NgbmodModalsModule,
    ReactiveFormsModule,
    StoreModule.forFeature('folders-route', reducer),
    PictureImageViewsModule,
    PaginationModule
  ]
})
export class FoldersModule {
}
