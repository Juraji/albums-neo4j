import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from '@environment';
import {EffectsModule} from '@ngrx/effects';
import {metaReducers, reducers} from "./@reducers";
import {HttpClientModule} from "@angular/common/http";
import {DirectoriesEffects} from "@effects/directories.effects";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NgbModule,
    StoreModule.forRoot(reducers, {metaReducers}),
    !environment.production ? StoreDevtoolsModule.instrument() : [],
    EffectsModule.forRoot([DirectoriesEffects])
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
