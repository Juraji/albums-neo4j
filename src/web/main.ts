import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {AppModule} from './app/app.module';
import {installExtensions} from '@utils/prototypes';
import {environment} from '@environment';
import {registerLocaleData} from '@angular/common';
import localeNl from '@angular/common/locales/nl';

installExtensions();
registerLocaleData(localeNl, 'nl');

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
