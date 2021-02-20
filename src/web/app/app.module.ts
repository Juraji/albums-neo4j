import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
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

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    NgbModule,
    NgbmodModalsModule.forRoot(),
    StoreModule.forRoot(reducers, {metaReducers}),
    !environment.production ? StoreDevtoolsModule.instrument() : [],
    EffectsModule.forRoot([DirectoriesEffects, PicturesEffects])
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
