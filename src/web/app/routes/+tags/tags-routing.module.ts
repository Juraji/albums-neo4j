import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TagsManagementPage} from './tags-management/tags-management.page';

const routes: Routes = [{path: '', component: TagsManagementPage}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TagsRoutingModule {
}
