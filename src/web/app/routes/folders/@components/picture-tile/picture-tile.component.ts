import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-picture-tile',
  templateUrl: './picture-tile.component.html',
  styleUrls: ['./picture-tile.component.scss']
})
export class PictureTileComponent implements OnInit {

  @Input()
  picture: InputVal<Picture>;

  @Input()
  folder: InputVal<Folder>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
