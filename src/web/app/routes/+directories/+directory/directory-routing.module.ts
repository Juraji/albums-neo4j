import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DirectoryPage} from './directory/directory.page';

const routes: Routes = [{ path: '', component: DirectoryPage }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DirectoryRoutingModule { }