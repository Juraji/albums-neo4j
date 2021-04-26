import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {TagManagementPage} from './tag-management/tag-management.page';

const routes: Routes = [
  { path: '', component: TagManagementPage },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TagsRoutingModule { }
