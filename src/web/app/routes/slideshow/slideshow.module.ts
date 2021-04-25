import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {SlideshowRoutingModule} from './slideshow-routing.module';
import {SlideshowPage} from './slideshow/slideshow.page';
import {MainNavbarModule} from '@components/main-navbar';
import {ReactiveFormsModule} from '@angular/forms';
import {SlideShowRunnerModal} from './@components/slide-show-runner/slide-show-runner.modal';
import {ModalsModule} from '@juraji/ng-bootstrap-modals';
import {PictureImageViewsModule} from '@components/picture-image-views';


@NgModule({
  declarations: [
    SlideshowPage,
    SlideShowRunnerModal
  ],
  imports: [
    CommonModule,
    SlideshowRoutingModule,
    MainNavbarModule,
    ReactiveFormsModule,
    ModalsModule,
    PictureImageViewsModule
  ]
})
export class SlideshowModule {
}
