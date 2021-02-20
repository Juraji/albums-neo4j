import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';

@Component({
  selector: 'app-directory-tree',
  templateUrl: './directory-tree.component.html',
  styleUrls: ['./directory-tree.component.scss']
})
export class DirectoryTreeComponent implements OnChanges {
  openedStates: Record<string, boolean> = {};
  isRoot = false;

  @Input()
  directories: Directory[] | null = null;

  @Input()
  showRootPaths = true;

  @Input()
  level = 0;

  @Output()
  readonly directoryAction = new EventEmitter<Directory>();

  constructor() {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.isRoot = this.level === 0;
  }

  toggleDirectory(directory: Directory) {
    this.openedStates = this.openedStates
      .copy({
        [directory.id]: !this.openedStates[directory.id]
      });
  }

  onDirectoryAction(directory: Directory) {
    this.directoryAction.emit(directory);
  }
}
