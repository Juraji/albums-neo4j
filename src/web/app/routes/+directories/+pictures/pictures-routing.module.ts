import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PicturePage} from './picture/picture.page';

const routes: Routes = [{path: ':pictureId', component: PicturePage}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PicturesRoutingModule {
}
