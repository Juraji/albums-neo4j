import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-picture-properties',
  templateUrl: './picture-properties.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PicturePropertiesComponent implements OnInit {

  @Input()
  picture: PictureProps | null = null;

  constructor() {
  }

  ngOnInit(): void {
  }

}
