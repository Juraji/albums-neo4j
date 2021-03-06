import {
  Directive,
  ElementRef,
  HostBinding,
  Input,
  OnChanges,
  OnInit,
  Renderer2,
  SecurityContext,
  SimpleChanges
} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {PicturesService} from '@services/pictures.service';

const CLASSNAMES = ['rounded', 'border', 'picture-thumbnail'];

@Directive({selector: 'img[appPictureThumbnail]'})
export class PictureThumbnailDirective implements OnInit, OnChanges {
  @Input() pictureId: BindingType<string>;
  @HostBinding('src') hostSrc: BindingType<string> = '#';

  constructor(
    private readonly picturesService: PicturesService,
    private readonly domSanitizer: DomSanitizer,
    private readonly elementRef: ElementRef,
    private readonly renderer: Renderer2,
  ) {
  }

  ngOnInit(): void {
    CLASSNAMES.forEach(cn => this.renderer.addClass(this.elementRef.nativeElement, cn));
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.hasOwnProperty('pictureId') && !!changes.pictureId.currentValue) {
      this.hostSrc = this.domSanitizer.sanitize(
          SecurityContext.URL,
          this.picturesService.getThumbnailUri(changes.pictureId.currentValue)
      );
    }
  }

}
