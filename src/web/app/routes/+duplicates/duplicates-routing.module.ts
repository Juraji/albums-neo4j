import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DuplicatesPage} from './duplicates/duplicates.page';

const routes: Routes = [{path: '', component: DuplicatesPage}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DuplicatesRoutingModule {
}
