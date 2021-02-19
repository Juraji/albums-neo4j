import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DirectoriesPage} from "./directories/directories.page";

const routes: Routes = [
  {path: '', component: DirectoriesPage},
  {path: ':directoryId', loadChildren: () => import('./+directory/directory.module').then(m => m.DirectoryModule)},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DirectoriesRoutingModule {
}
