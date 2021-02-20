import {InjectionToken} from '@angular/core';

export const WINDOW = new InjectionToken<Window>('host-window');

export const hostWindowFactory = () => window;
