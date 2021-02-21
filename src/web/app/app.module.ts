import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from '@environment';
import {EffectsModule} from '@ngrx/effects';
import {metaReducers, reducers} from './@reducers';
import {HttpClientModule} from '@angular/common/http';
import {DirectoriesEffects} from '@effects/directories.effects';
import {PicturesEffects} from '@effects/pictures.effects';
import {NgbmodModalsModule} from '@juraji/ng-bootstrap-modals';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {hostWindowFactory, WINDOW} from '@services/provided/window.injection-token';
import {ROOT_EM_SIZE, rootEmSizeFactory} from '@services/provided/rem-size.injection-token';
import {DOCUMENT} from '@angular/common';
import {TagsEffects} from '@effects/tags.effects';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    NgbmodModalsModule.forRoot(),
    StoreModule.forRoot(reducers, {metaReducers}),
    !environment.production ? StoreDevtoolsModule.instrument() : [],
    EffectsModule.forRoot([DirectoriesEffects, PicturesEffects, TagsEffects])
  ],
  providers: [
    {provide: WINDOW, useFactory: hostWindowFactory},
    {provide: ROOT_EM_SIZE, useFactory: rootEmSizeFactory, deps: [DOCUMENT]},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
