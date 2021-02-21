import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-picture-properties',
  templateUrl: './picture-properties.component.html',
})
export class PicturePropertiesComponent implements OnInit {

  @Input()
  picture: PictureProps | null = null;

  constructor() {
  }

  ngOnInit(): void {
  }

}
