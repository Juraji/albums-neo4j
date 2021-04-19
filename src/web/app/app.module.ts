import {LOCALE_ID, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from '@environment';
import {EffectsModule} from '@ngrx/effects';
import {ROOT_META_REDUCERS, ROOT_REDUCER, ROOT_EFFECTS} from '@ngrx/root';
import {HttpClientModule} from '@angular/common/http';
import {ModalsModule} from '@juraji/ng-bootstrap-modals';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
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
    ModalsModule.forRoot(),
    StoreModule.forRoot(ROOT_REDUCER, {metaReducers: ROOT_META_REDUCERS}),
    StoreRouterConnectingModule.forRoot(),
    !environment.production ? StoreDevtoolsModule.instrument() : [],
    EffectsModule.forRoot(ROOT_EFFECTS)
  ],
  bootstrap: [AppComponent],
  providers: [
    {provide: LOCALE_ID, useValue: 'nl'}
  ]
})
export class AppModule {
}
