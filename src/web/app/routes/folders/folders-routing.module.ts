import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {FolderPage} from './folder/folder.page';
import {ROOT_FOLDER_ID} from '@services/folders.service';

const routes: Routes = [
  {
    path: ':folderId',
    component: FolderPage
  },
  {
    path: ':folderId/pictures',
    loadChildren: () => import('./pictures/pictures.module').then(m => m.PicturesModule)
  },
  {
    path: '**',
    redirectTo: ROOT_FOLDER_ID
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FoldersRoutingModule {
}
