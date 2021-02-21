import {Directive, ElementRef, Renderer2} from '@angular/core';
import {PictureImgDirective} from './picture-img.directive';
import {DomSanitizer} from '@angular/platform-browser';

@Directive({
  selector: '[appPictureFullView]'
})
export class PictureFullViewDirective extends PictureImgDirective {
  constructor(
    domSanitizer: DomSanitizer,
    elementRef: ElementRef,
    renderer: Renderer2,
  ) {
    super(domSanitizer, elementRef, renderer);
  }

  staticClassList(): string[] {
    return ['rounded', 'shadow', 'border', 'picture-full-view'];
  }

}
