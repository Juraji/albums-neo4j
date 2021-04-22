import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ManageDuplicatesPage} from './manage-duplicates/manage-duplicates.page';

const routes: Routes = [
  {path: '', component: ManageDuplicatesPage},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DuplicatesRoutingModule {
}
