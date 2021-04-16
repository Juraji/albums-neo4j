import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {FolderPage} from './folder/folder.page';
import {ROOT_FOLDER} from './root-folder';

const routes: Routes = [
  {path: ':folderId', component: FolderPage},
  {path: '**', redirectTo: ROOT_FOLDER.id}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FoldersRoutingModule {
}