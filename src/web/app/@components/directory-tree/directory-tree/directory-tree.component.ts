import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-directory-tree',
  templateUrl: './directory-tree.component.html',
  styleUrls: ['./directory-tree.component.scss']
})
export class DirectoryTreeComponent {

  @Input()
  directories: Directory[] | null = []

  @Input()
  level: number = 0

  openedStates: Record<string, boolean> = {}

  constructor() {
  }

  toggleDirectory(directory: Directory) {
    this.openedStates = this.openedStates
      .copy({
        [directory.id]: !this.openedStates[directory.id]
      })
  }
}
