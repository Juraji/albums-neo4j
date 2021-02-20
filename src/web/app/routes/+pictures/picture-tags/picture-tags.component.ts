import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-picture-tags',
  templateUrl: './picture-tags.component.html',
})
export class PictureTagsComponent implements OnInit {

  @Input()
  picture: PictureProps | null = null;

  constructor() {
  }

  ngOnInit(): void {
  }

}
