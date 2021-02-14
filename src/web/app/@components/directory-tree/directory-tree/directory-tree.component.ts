import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-directory-tree',
  templateUrl: './directory-tree.component.html',
  styleUrls: ['./directory-tree.component.scss']
})
export class DirectoryTreeComponent {
  openedStates: Record<string, boolean> = {}

  @Input()
  directories: Directory[] | null = []

  @Input()
  level: number = 0

  @Output()
  readonly directoryAction = new EventEmitter<Directory>()

  constructor() {
  }

  toggleDirectory(directory: Directory) {
    this.openedStates = this.openedStates
      .copy({
        [directory.id]: !this.openedStates[directory.id]
      })
  }

  onDirectoryAction(directory: Directory) {
    this.directoryAction.emit(directory)
  }
}
