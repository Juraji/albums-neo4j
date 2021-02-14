import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DirectoryTreeComponent} from './directory-tree/directory-tree.component';


@NgModule({
  imports: [CommonModule],
  declarations: [DirectoryTreeComponent],
  exports: [DirectoryTreeComponent]
})
export class DirectoryTreeModule {
}
