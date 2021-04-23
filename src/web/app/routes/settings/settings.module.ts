import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {SettingsRoutingModule} from './settings-routing.module';
import {SettingsPage} from './settings/settings.page';
import {MainNavbarModule} from '@components/main-navbar';


@NgModule({
  declarations: [
    SettingsPage,
  ],
  imports: [
    CommonModule,
    SettingsRoutingModule,
    MainNavbarModule
  ]
})
export class SettingsModule {
}
