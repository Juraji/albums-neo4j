import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-picture-tile',
  templateUrl: './picture-tile.component.html',
  styleUrls: ['./picture-tile.component.scss']
})
export class PictureTileComponent implements OnInit {

  @Input()
  picture: BindingType<Picture>;

  @Input()
  folder: BindingType<Folder>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
