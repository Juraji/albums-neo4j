import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {FoldersRoutingModule} from './folders-routing.module';
import {MainNavbarModule} from '@components/main-navbar/main-navbar.module';
import {FolderPage} from './folder/folder.page';
import {UtilityPipesModule} from '@components/utility-pipes/utility-pipes.module';
import {AddFolderModal} from './@components/add-folder-modal/add-folder.modal';
import {ModalsModule} from '@juraji/ng-bootstrap-modals';
import {ReactiveFormsModule} from '@angular/forms';
import {FolderControlsComponent} from './@components/folder-controls/folder-controls.component';
import {AddPicturesModal} from './@components/add-pictures-modal/add-pictures.modal';
import {PictureImageViewsModule} from '@components/picture-image-views';
import {PaginationModule} from '@components/pagination/pagination.module';
import {FolderSelectorModule} from '@components/folder-selector';
import {FolderBreadcrumbsComponent} from './@components/folder-breadcrumbs/folder-breadcrumbs.component';
import {FolderTileComponent} from './@components/folder-tile/folder-tile.component';
import {PictureTileComponent} from './@components/picture-tile/picture-tile.component';
import {StoreModule} from '@ngrx/store';
import {folderRouteReducer} from './@ngrx';
import {ContextMenuModule} from 'ngx-contextmenu';
import {TagMgmtModule} from '@components/tag-mgmt';


@NgModule({
  declarations: [
    FolderPage,
    AddFolderModal,
    FolderBreadcrumbsComponent,
    FolderControlsComponent,
    AddPicturesModal,
    FolderTileComponent,
    PictureTileComponent,
  ],
  imports: [
    CommonModule,
    FoldersRoutingModule,
    MainNavbarModule,
    UtilityPipesModule,
    ModalsModule,
    ReactiveFormsModule,
    PictureImageViewsModule,
    PaginationModule,
    FolderSelectorModule,
    StoreModule.forFeature('foldersRoute', folderRouteReducer),
    ContextMenuModule,
    TagMgmtModule,
  ]
})
export class FoldersModule {
}
