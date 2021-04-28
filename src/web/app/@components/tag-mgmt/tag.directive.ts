import {Directive, ElementRef, HostBinding, Input, OnChanges, OnInit, Renderer2, SimpleChanges} from '@angular/core';

@Directive({
  selector: 'span[appTag],button[appTag]'
})
export class TagDirective implements OnInit, OnChanges {
  private static readonly staticClassList = ['badge', 'tag-badge'];

  @Input()
  tag: BindingType<Tag>;

  @Input()
  @HostBinding('style.fontSize')
  fontSize: BindingType<string>;

  @HostBinding('style.backgroundColor')
  backgroundColor: BindingType<string>;

  @HostBinding('style.color')
  color: BindingType<string>;

  constructor(
    private readonly elementRef: ElementRef,
    private readonly renderer: Renderer2,
  ) {
  }

  ngOnInit(): void {
    TagDirective.staticClassList.forEach(cn =>
      this.renderer.addClass(this.elementRef.nativeElement, cn));
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.hasOwnProperty('tag') && !!changes.tag.currentValue) {
      const t: Tag = changes.tag.currentValue;
      this.backgroundColor = t.color;
      this.color = t.textColor;
      this.renderer.setProperty(this.elementRef.nativeElement, 'innerText', t.label);
    }
  }

}
