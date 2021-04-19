import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {FoldersRoutingModule} from './folders-routing.module';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import {FolderPage} from './folder/folder.page';
import {UtilityPipesModule} from '@components/utility-pipes/utility-pipes.module';
import {AddFolderModal} from './@components/add-folder-modal/add-folder.modal';
import {ModalsModule} from '@juraji/ng-bootstrap-modals';
import {ReactiveFormsModule} from '@angular/forms';
import {StoreModule} from '@ngrx/store';
import {reducer} from './@ngrx';
import {FolderControlsComponent} from './@components/folder-controls/folder-controls.component';
import {AddPicturesModal} from './@components/add-pictures-modal/add-pictures.modal';
import {PictureImageViewsModule} from '@components/picture-image-views';
import {PaginationModule} from '@components/pagination/pagination.module';
import {FolderSelectorModule} from '@components/folder-selector';
import {FolderBreadcrumbsComponent} from './@components/folder-breadcrumbs/folder-breadcrumbs.component';


@NgModule({
  declarations: [
    FolderPage,
    AddFolderModal,
    FolderBreadcrumbsComponent,
    FolderControlsComponent,
    AddPicturesModal,
  ],
  imports: [
    CommonModule,
    FoldersRoutingModule,
    MainNavbarModule,
    UtilityPipesModule,
    ModalsModule,
    ReactiveFormsModule,
    StoreModule.forFeature('folders-route', reducer),
    PictureImageViewsModule,
    PaginationModule,
    FolderSelectorModule,
  ]
})
export class FoldersModule {
}
