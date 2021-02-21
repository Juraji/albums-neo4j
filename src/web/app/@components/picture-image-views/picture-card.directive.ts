import {Directive, ElementRef, Renderer2} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {PictureImgDirective} from './picture-img.directive';

@Directive({
  selector: 'img[appPictureCard]'
})
export class PictureCardDirective extends PictureImgDirective {
  constructor(
    domSanitizer: DomSanitizer,
    elementRef: ElementRef,
    renderer: Renderer2,
  ) {
    super(domSanitizer, elementRef, renderer);
  }

  staticClassList(): string[] {
    return ['rounded', 'shadow', 'border', 'img-fluid', 'picture-card'];
  }
}
