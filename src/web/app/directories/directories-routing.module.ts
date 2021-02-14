import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DirectoriesOverviewPage} from "./directories-overview/directories-overview.page";
import {DirectoryPage} from "./directory/directory.page";

const routes: Routes = [
  {path: '', component: DirectoriesOverviewPage},
  {path: ':directoryId', component: DirectoryPage},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DirectoriesRoutingModule {
}
