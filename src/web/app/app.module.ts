import {LOCALE_ID, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from '@environment';
import {EffectsModule} from '@ngrx/effects';
import {metaReducers, reducers} from './@reducers';
import {HttpClientModule} from '@angular/common/http';
import {FoldersEffects} from '@effects/folders.effects';
import {PicturesEffects} from '@effects/pictures.effects';
import {NgbmodModalsModule} from '@juraji/ng-bootstrap-modals';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TagsEffects} from '@effects/tags.effects';
import {DuplicatesEffects} from '@effects/duplicates.effects';
import {StoreRouterConnectingModule} from '@ngrx/router-store';

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
    StoreRouterConnectingModule.forRoot(),
    !environment.production ? StoreDevtoolsModule.instrument() : [],
    EffectsModule.forRoot([FoldersEffects, PicturesEffects, TagsEffects, DuplicatesEffects])
  ],
  bootstrap: [AppComponent],
  providers: [
    {provide: LOCALE_ID, useValue: 'nl'}
  ]
})
export class AppModule {
}
