import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SlideshowPage} from './slideshow/slideshow.page';

const routes: Routes = [{path: '', component: SlideshowPage}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SlideshowRoutingModule {
}
