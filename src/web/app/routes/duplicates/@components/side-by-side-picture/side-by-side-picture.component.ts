import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-side-by-side-picture',
  templateUrl: './side-by-side-picture.component.html',
  styleUrls: ['./side-by-side-picture.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideBySidePictureComponent {

  @Input()
  public picture: BindingType<Picture>;

  @Output()
  readonly unlinkDuplicateClick = new EventEmitter<Picture>();

  @Output()
  readonly deletePictureClick = new EventEmitter<Picture>();

  @Output()
  readonly movePictureClick = new EventEmitter<Picture>();

  onUnlinkDuplicate() {
    if (!!this.picture) {
      this.unlinkDuplicateClick.emit(this.picture);
    }
  }

  onDeletePicture() {
    if (!!this.picture) {
      this.deletePictureClick.emit(this.picture);
    }
  }

  onMovePicture() {
    if (!!this.picture) {
      this.movePictureClick.emit(this.picture);
    }
  }
}
