import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-folder-tile',
  templateUrl: './folder-tile.component.html',
  styleUrls: ['./folder-tile.component.scss']
})
export class FolderTileComponent implements OnInit {

  @Input()
  folder: BindingType<Folder>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
