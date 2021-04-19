import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PictureViewPage} from './picture-view/picture-view.page';

const routes: Routes = [
  {
    path: ':pictureId',
    component: PictureViewPage
  },
  {
    path: '**',
    redirectTo: '/'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PicturesRoutingModule {
}
