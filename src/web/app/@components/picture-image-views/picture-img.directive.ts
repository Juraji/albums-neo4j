import {
  Directive,
  ElementRef,
  HostBinding,
  Input, OnChanges,
  OnInit,
  Renderer2,
  SecurityContext,
  SimpleChanges
} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {environment} from '@environment';

const CLASSNAMES = ['rounded', 'border', 'picture-img'];

@Directive({selector: 'img[appPictureImg]'})
export class PictureImgDirective implements OnInit, OnChanges {
  @Input() picture: PictureProps | null = null;
  @HostBinding('src') hostSrc: string | null = null;
  @HostBinding('title') hostTitle: string | null = null;

  constructor(
    private readonly domSanitizer: DomSanitizer,
    private readonly elementRef: ElementRef,
    private readonly renderer: Renderer2,
  ) {
  }

  ngOnInit(): void {
    CLASSNAMES.forEach(cn => this.renderer.addClass(this.elementRef.nativeElement, cn));
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.hasOwnProperty('picture') && !!changes.picture.currentValue) {
      this.hostSrc = this.getSanitizedSrc(changes.picture.currentValue);
      this.hostTitle = changes.picture.currentValue.name;
    }
  }

  private getSanitizedSrc(picture: PictureProps) {
    return this.domSanitizer.sanitize(
      SecurityContext.URL,
      `${environment.apiBaseUri}/pictures/${picture.id}/files/image`
    );
  }

}
