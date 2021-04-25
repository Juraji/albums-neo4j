import {animate, style, transition, trigger} from '@angular/animations';

export const slideshowAnimation = trigger('imageChange', [
  transition(':enter', [
    style({transform: 'translateX(100vw)'}),
    animate('300ms ease-in-out')
  ]),
  transition(':leave', [
    animate('300ms ease-in-out', style({transform: 'translateX(-100vw)'}))
  ]),
]);
