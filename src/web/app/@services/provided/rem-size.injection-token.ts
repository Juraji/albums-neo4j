import {InjectionToken} from '@angular/core';

export const ROOT_EM_SIZE = new InjectionToken<number>('root-em-pixels-size');

export const rootEmSizeFactory = (document: Document): number => {
  const fontSizeProp = window.getComputedStyle(document.body, null).fontSize;
  return parseInt(fontSizeProp.substr(0, fontSizeProp.length - 2), 10);
};
